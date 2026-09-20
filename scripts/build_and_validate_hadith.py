#!/usr/bin/env python3
"""
Hadith Dataset Builder & Validator for Al-Qur'an Offline
Source: Hadits Arba'in An-Nawawi (authentic 42 hadiths)
Kitab: Al-Arba'in An-Nawawiyyah karya Imam An-Nawawi
"""

import json
import os
import sqlite3
import hashlib
import urllib.request
import sys

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets")
HADITH_DIR = os.path.join(ASSETS_DIR, "hadith")
DB_PATH = os.path.join(ASSETS_DIR, "quran.db")
METADATA_PATH = os.path.join(ASSETS_DIR, "hadith_metadata.json")
HADITHS_JSON_PATH = os.path.join(HADITH_DIR, "hadiths.json")

NARRATORS = {
    1: "HR. Bukhari & Muslim",
    2: "HR. Muslim (Hadits Jibril)",
    3: "HR. Bukhari & Muslim",
    4: "HR. Bukhari & Muslim",
    5: "HR. Bukhari & Muslim",
    6: "HR. Bukhari & Muslim",
    7: "HR. Muslim",
    8: "HR. Bukhari & Muslim",
    9: "HR. Bukhari & Muslim",
    10: "HR. Muslim",
    11: "HR. At-Tirmidzi & An-Nasa'i",
    12: "HR. At-Tirmidzi",
    13: "HR. Bukhari & Muslim",
    14: "HR. Bukhari & Muslim",
    15: "HR. Bukhari & Muslim",
    16: "HR. Bukhari",
    17: "HR. Muslim",
    18: "HR. At-Tirmidzi",
    19: "HR. At-Tirmidzi",
    20: "HR. Bukhari",
    21: "HR. Muslim",
    22: "HR. Muslim",
    23: "HR. Muslim",
    24: "HR. Muslim",
    25: "HR. Muslim",
    26: "HR. Bukhari & Muslim",
    27: "HR. Muslim & Ahmad",
    28: "HR. Abu Dawud & At-Tirmidzi",
    29: "HR. At-Tirmidzi",
    30: "HR. Ad-Daruquthni",
    31: "HR. Ibnu Majah",
    32: "HR. Ibnu Majah & Ahmad",
    33: "HR. Al-Baihaqi",
    34: "HR. Muslim",
    35: "HR. Muslim",
    36: "HR. Muslim",
    37: "HR. Bukhari & Muslim",
    38: "HR. Bukhari",
    39: "HR. Ibnu Majah & Al-Baihaqi",
    40: "HR. Bukhari",
    41: "HR. Al-Baghawi",
    42: "HR. At-Tirmidzi"
}

def build():
    os.makedirs(HADITH_DIR, exist_ok=True)

    print("Fetching authentic 42 Hadits Arba'in An-Nawawi...")
    url = "https://raw.githubusercontent.com/abdanzamzam/arbain-id/main/data/hadits.json"
    req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0 (Hadith-Builder)"})
    with urllib.request.urlopen(req) as resp:
        raw_data = json.loads(resp.read().decode("utf-8"))

    sorted_raw = sorted(raw_data, key=lambda x: x["no"])
    assert len(sorted_raw) == 42, f"Expected 42 hadiths, got {len(sorted_raw)}"

    cleaned_hadiths = []
    for idx, item in enumerate(sorted_raw, start=1):
        assert item["no"] == idx, f"Sequence error: {item['no']} != {idx}"
        title = item["title"].strip()
        teks_ar = item["ar"].strip()
        teks_id = item["id"].strip()
        sumber = NARRATORS.get(idx, "HR. Perawi Shahih")

        assert len(title) > 0, f"Empty title in hadith {idx}"
        assert len(teks_ar) > 0, f"Empty arabic text in hadith {idx}"
        assert len(teks_id) > 0, f"Empty indonesian translation in hadith {idx}"

        hadith_obj = {
            "id": idx,
            "kitab": "Hadits Arba'in An-Nawawi",
            "nomor": idx,
            "judul": title,
            "sumber": sumber,
            "teks_ar": teks_ar,
            "teks_id": teks_id,
            "tema": title
        }
        cleaned_hadiths.append(hadith_obj)

    # Write hadiths.json
    with open(HADITHS_JSON_PATH, "w", encoding="utf-8") as f:
        json.dump(cleaned_hadiths, f, ensure_ascii=False, indent=2)

    # Insert into SQLite Database
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS hadiths (
        id INTEGER PRIMARY KEY,
        kitab TEXT NOT NULL,
        nomor INTEGER NOT NULL,
        judul TEXT NOT NULL,
        sumber TEXT NOT NULL,
        teks_ar TEXT NOT NULL,
        teks_id TEXT NOT NULL,
        tema TEXT NOT NULL
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS hadith_bookmarks (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        hadith_id INTEGER NOT NULL,
        created_at INTEGER NOT NULL,
        note TEXT NOT NULL DEFAULT '',
        FOREIGN KEY (hadith_id) REFERENCES hadiths(id) ON DELETE CASCADE
    );
    """)

    cursor.execute("DELETE FROM hadiths;")
    for h in cleaned_hadiths:
        cursor.execute("""
        INSERT INTO hadiths (id, kitab, nomor, judul, sumber, teks_ar, teks_id, tema)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            h["id"],
            h["kitab"],
            h["nomor"],
            h["judul"],
            h["sumber"],
            h["teks_ar"],
            h["teks_id"],
            h["tema"]
        ))

    cursor.execute("CREATE INDEX IF NOT EXISTS idx_hadiths_nomor ON hadiths(nomor);")
    cursor.execute("CREATE INDEX IF NOT EXISTS idx_hadiths_judul ON hadiths(judul);")

    conn.commit()
    conn.close()

    # Calculate SHA256 of hadiths.json
    with open(HADITHS_JSON_PATH, "rb") as f:
        json_hash = hashlib.sha256(f.read()).hexdigest()

    metadata = {
        "dataset_name": "Hadits Arba'in An-Nawawi (42 Hadits)",
        "author": "Imam Yahya bin Syaraf An-Nawawi",
        "total_hadiths": 42,
        "language": "Arab & Indonesia",
        "source": "Kitab Al-Arba'in An-Nawawiyyah (abdanzamzam/arbain-id)",
        "license": "Permissive (ISC / Public Domain Hadith)",
        "sha256_checksum": json_hash
    }

    with open(METADATA_PATH, "w", encoding="utf-8") as f:
        json.dump(metadata, f, ensure_ascii=False, indent=2)

    print(f"[SUCCESS] Hadith dataset built successfully!")
    print(f"Total Hadiths: {len(cleaned_hadiths)}")
    print(f"JSON: {HADITHS_JSON_PATH}")
    print(f"Metadata: {METADATA_PATH}")
    print(f"SHA-256: {json_hash}")

if __name__ == "__main__":
    build()
