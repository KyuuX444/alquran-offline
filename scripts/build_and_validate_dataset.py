#!/usr/bin/env python3
"""
Dataset Builder & Validator for Al-Qur'an Offline
Source: Tanzil Uthmani text & Kemenag Indonesian translation (via verified quran-json distribution)
Total Chapters: 114
Total Verses: 6,236
"""

import json
import os
import sqlite3
import hashlib
import urllib.request
import sys

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets")
DB_PATH = os.path.join(ASSETS_DIR, "quran.db")
METADATA_PATH = os.path.join(ASSETS_DIR, "quran_metadata.json")

# Standard 30 Juz boundaries: (juz_num, start_surah, start_verse, end_surah, end_verse)
JUZ_BOUNDARIES = [
    (1, 1, 1, 2, 141),
    (2, 2, 142, 2, 252),
    (3, 2, 253, 3, 92),
    (4, 3, 93, 4, 23),
    (5, 4, 24, 4, 147),
    (6, 4, 148, 5, 81),
    (7, 5, 82, 6, 110),
    (8, 6, 111, 7, 87),
    (9, 7, 88, 8, 40),
    (10, 8, 41, 9, 92),
    (11, 9, 93, 11, 5),
    (12, 11, 6, 12, 52),
    (13, 12, 53, 14, 52),
    (14, 15, 1, 16, 128),
    (15, 17, 1, 18, 74),
    (16, 18, 75, 20, 135),
    (17, 21, 1, 22, 78),
    (18, 23, 1, 25, 20),
    (19, 25, 21, 27, 55),
    (20, 27, 56, 29, 45),
    (21, 29, 46, 33, 30),
    (22, 33, 31, 36, 27),
    (23, 36, 28, 39, 31),
    (24, 39, 32, 41, 46),
    (25, 41, 47, 45, 37),
    (26, 46, 1, 51, 30),
    (27, 51, 31, 57, 29),
    (28, 58, 1, 66, 12),
    (29, 67, 1, 77, 50),
    (30, 78, 1, 114, 6),
]

def get_juz_for_ayah(surah_id, verse_id):
    for juz_num, s_s, s_v, e_s, e_v in JUZ_BOUNDARIES:
        # Check if (surah_id, verse_id) falls between (s_s, s_v) and (e_s, e_v)
        start_cond = (surah_id > s_s) or (surah_id == s_s and verse_id >= s_v)
        end_cond = (surah_id < e_s) or (surah_id == e_s and verse_id <= e_v)
        if start_cond and end_cond:
            return juz_num
    return 30

def fetch_surah(surah_id):
    url = f"https://raw.githubusercontent.com/risan/quran-json/master/dist/chapters/id/{surah_id}.json"
    req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0 (AlQuran-Offline-Builder)"})
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read().decode("utf-8"))

def build_dataset():
    os.makedirs(ASSETS_DIR, exist_ok=True)
    if os.path.exists(DB_PATH):
        os.remove(DB_PATH)

    print("Fetching and assembling 114 surahs...")
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    # Create tables
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS surahs (
        id INTEGER PRIMARY KEY,
        name_ar TEXT NOT NULL,
        name_latin TEXT NOT NULL,
        translation_id TEXT NOT NULL,
        type TEXT NOT NULL,
        total_verses INTEGER NOT NULL,
        juz_start INTEGER NOT NULL,
        juz_end INTEGER NOT NULL
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS ayahs (
        id INTEGER PRIMARY KEY,
        surah_id INTEGER NOT NULL,
        verse_id INTEGER NOT NULL,
        juz_id INTEGER NOT NULL,
        text_ar TEXT NOT NULL,
        text_id TEXT NOT NULL,
        transliteration TEXT NOT NULL,
        FOREIGN KEY (surah_id) REFERENCES surahs(id) ON DELETE CASCADE
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS bookmarks (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        surah_id INTEGER NOT NULL,
        verse_id INTEGER NOT NULL,
        created_at INTEGER NOT NULL,
        note TEXT NOT NULL DEFAULT ''
    );
    """)

    # Room required metadata table
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS room_master_table (
        id INTEGER PRIMARY KEY,
        identity_hash TEXT
    );
    """)
    cursor.execute("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES(42, 'b08493cfbc20cfb584a7536d29ec4188')")

    all_surahs_metadata = []
    total_verses_count = 0
    ayah_global_id = 1

    for s_id in range(1, 115):
        data = fetch_surah(s_id)
        
        # Validation checks
        assert data["id"] == s_id, f"Surah ID mismatch: {data['id']} != {s_id}"
        verses = data.get("verses", [])
        assert len(verses) == data["total_verses"], f"Verse count mismatch in Surah {s_id}"
        
        # Determine starting and ending Juz for this surah
        juz_start = get_juz_for_ayah(s_id, 1)
        juz_end = get_juz_for_ayah(s_id, len(verses))

        surah_type = "Makkiyah" if data.get("type") == "meccan" else "Madaniyah"
        
        cursor.execute("""
        INSERT INTO surahs (id, name_ar, name_latin, translation_id, type, total_verses, juz_start, juz_end)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            s_id,
            data["name"].strip(),
            data["transliteration"].strip(),
            data["translation"].strip(),
            surah_type,
            data["total_verses"],
            juz_start,
            juz_end
        ))

        all_surahs_metadata.append({
            "id": s_id,
            "name_ar": data["name"].strip(),
            "name_latin": data["transliteration"].strip(),
            "translation_id": data["translation"].strip(),
            "type": surah_type,
            "total_verses": data["total_verses"],
            "juz_start": juz_start,
            "juz_end": juz_end
        })

        for idx, v in enumerate(verses, start=1):
            assert v["id"] == idx, f"Ayah order mismatch in Surah {s_id}: {v['id']} != {idx}"
            text_ar = v.get("text", "").strip()
            text_id = v.get("translation", "").strip()
            transliteration = v.get("transliteration", "").strip()
            
            assert text_ar, f"Empty Arabic text in {s_id}:{idx}"
            assert text_id, f"Empty Indonesian translation in {s_id}:{idx}"
            assert "\ufffd" not in text_ar, f"Invalid unicode in {s_id}:{idx}"
            
            juz_id = get_juz_for_ayah(s_id, idx)

            cursor.execute("""
            INSERT INTO ayahs (id, surah_id, verse_id, juz_id, text_ar, text_id, transliteration)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """, (
                ayah_global_id,
                s_id,
                idx,
                juz_id,
                text_ar,
                text_id,
                transliteration
            ))
            ayah_global_id += 1
            total_verses_count += 1

        if s_id % 20 == 0 or s_id == 114:
            print(f"Processed Surah {s_id}/114 (Cumulative Verses: {total_verses_count})")

    # Create Indexes
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_ayahs_surah_verse ON ayahs(surah_id, verse_id);")
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_ayahs_juz ON ayahs(juz_id);")
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_surahs_name ON surahs(name_latin);")

    conn.commit()
    conn.close()

    assert total_verses_count == 6236, f"Total verses {total_verses_count} != 6236"
    assert len(all_surahs_metadata) == 114, f"Total surahs {len(all_surahs_metadata)} != 114"

    # Calculate SHA-256 of database
    with open(DB_PATH, "rb") as f:
        db_hash = hashlib.sha256(f.read()).hexdigest()

    # Juz summary for quick navigation
    juz_summary = []
    for juz_num, s_s, s_v, e_s, e_v in JUZ_BOUNDARIES:
        start_surah = all_surahs_metadata[s_s - 1]
        end_surah = all_surahs_metadata[e_s - 1]
        juz_summary.append({
            "juz": juz_num,
            "start_surah_id": s_s,
            "start_surah_name": start_surah["name_latin"],
            "start_verse": s_v,
            "end_surah_id": e_s,
            "end_surah_name": end_surah["name_latin"],
            "end_verse": e_v,
        })

    metadata = {
        "dataset_name": "Al-Qur'an 30 Juz & 114 Surah (Offline)",
        "arabic_source": "Tanzil Project (Uthmani Text)",
        "arabic_license": "Creative Commons Attribution 3.0",
        "translation_source": "Kementerian Agama Republik Indonesia (Kemenag RI)",
        "translation_language": "Bahasa Indonesia",
        "total_surahs": 114,
        "total_verses": 6236,
        "total_juz": 30,
        "sha256_checksum": db_hash,
        "surahs": all_surahs_metadata,
        "juz": juz_summary
    }

    with open(METADATA_PATH, "w", encoding="utf-8") as f:
        json.dump(metadata, f, ensure_ascii=False, indent=2)

    db_size_mb = os.path.getsize(DB_PATH) / (1024 * 1024)
    print(f"\n[SUCCESS] Dataset generated successfully!")
    print(f"Database: {DB_PATH} ({db_size_mb:.2f} MB)")
    print(f"Metadata: {METADATA_PATH}")
    print(f"Database SHA-256: {db_hash}")
    print(f"Total Surahs: 114")
    print(f"Total Verses: {total_verses_count}")

if __name__ == "__main__":
    build_dataset()
