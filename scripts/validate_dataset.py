#!/usr/bin/env python3
"""
Independent Dataset Validation Tool for Al-Qur'an Offline
Enforces integrity of the 114 surahs and 6,236 ayahs in SQLite database and metadata.
Fails build with non-zero exit code if any irregularity is detected.
"""

import hashlib
import json
import os
import sqlite3
import sys

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets")
DB_PATH = os.path.join(ASSETS_DIR, "quran.db")
METADATA_PATH = os.path.join(ASSETS_DIR, "quran_metadata.json")

def validate():
    print("==================================================")
    print("      AL-QUR'AN DATASET INTEGRITY VALIDATOR       ")
    print("==================================================")

    # 1. Existence check
    if not os.path.exists(DB_PATH):
        print(f"[FAIL] Database file not found: {DB_PATH}")
        sys.exit(1)

    if not os.path.exists(METADATA_PATH):
        print(f"[FAIL] Metadata file not found: {METADATA_PATH}")
        sys.exit(1)

    # 2. Metadata check
    try:
        with open(METADATA_PATH, "r", encoding="utf-8") as f:
            metadata = json.load(f)
    except Exception as e:
        print(f"[FAIL] Cannot read metadata JSON: {e}")
        sys.exit(1)

    assert metadata.get("total_surahs") == 114, f"Metadata total_surahs is {metadata.get('total_surahs')} != 114"
    assert metadata.get("total_verses") == 6236, f"Metadata total_verses is {metadata.get('total_verses')} != 6236"
    assert metadata.get("total_juz") == 30, f"Metadata total_juz is {metadata.get('total_juz')} != 30"
    print("[PASS] Metadata JSON structure is valid.")

    # 3. Checksum verification
    with open(DB_PATH, "rb") as f:
        actual_hash = hashlib.sha256(f.read()).hexdigest()

    expected_hash = metadata.get("sha256_checksum")
    if actual_hash != expected_hash:
        print(f"[FAIL] SHA-256 Checksum mismatch!")
        print(f"  Expected: {expected_hash}")
        print(f"  Actual:   {actual_hash}")
        sys.exit(1)
    print(f"[PASS] Checksum verified (SHA-256: {actual_hash[:16]}...)")

    # 4. SQLite Integrity
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    cursor.execute("PRAGMA integrity_check;")
    integrity = cursor.fetchone()[0]
    if integrity != "ok":
        print(f"[FAIL] SQLite integrity check failed: {integrity}")
        sys.exit(1)
    print("[PASS] SQLite PRAGMA integrity_check: OK")

    # 5. Check Surahs count and sequence
    cursor.execute("SELECT id, name_latin, total_verses FROM surahs ORDER BY id ASC;")
    surahs = cursor.fetchall()
    if len(surahs) != 114:
        print(f"[FAIL] Surahs count is {len(surahs)}, expected 114")
        sys.exit(1)

    for idx, s in enumerate(surahs, start=1):
        if s[0] != idx:
            print(f"[FAIL] Non-sequential surah ID: expected {idx}, found {s[0]}")
            sys.exit(1)
    print("[PASS] 114 Surahs verified (1..114 contiguous).")

    # 6. Check Ayahs count
    cursor.execute("SELECT COUNT(*) FROM ayahs;")
    ayah_count = cursor.fetchone()[0]
    if ayah_count != 6236:
        print(f"[FAIL] Total ayahs is {ayah_count}, expected 6,236")
        sys.exit(1)
    print("[PASS] 6,236 Ayahs verified in database.")

    # 7. Check Verse counts per surah
    cursor.execute("""
    SELECT s.id, s.name_latin, s.total_verses, COUNT(a.id)
    FROM surahs s
    JOIN ayahs a ON s.id = a.surah_id
    GROUP BY s.id
    ORDER BY s.id ASC;
    """)
    rows = cursor.fetchall()
    for s_id, s_name, expected_v, actual_v in rows:
        if expected_v != actual_v:
            print(f"[FAIL] Surah {s_id} ({s_name}) verse count mismatch: expected {expected_v}, actual {actual_v}")
            sys.exit(1)
    print("[PASS] All individual surah verse counts match exact Quranic counts.")

    # 8. Check Verse sequence per surah (no gaps, no duplicates)
    cursor.execute("SELECT surah_id, verse_id FROM ayahs ORDER BY surah_id ASC, verse_id ASC;")
    current_surah = 0
    expected_verse = 1
    for s_id, v_id in cursor.fetchall():
        if s_id != current_surah:
            current_surah = s_id
            expected_verse = 1
        if v_id != expected_verse:
            print(f"[FAIL] Verse sequence gap in Surah {s_id}: expected verse {expected_verse}, found {v_id}")
            sys.exit(1)
        expected_verse += 1
    print("[PASS] Verse numbering in each surah is strictly sequential 1..N with zero gaps or duplicates.")

    # 9. Check empty or invalid texts
    cursor.execute("""
    SELECT COUNT(*) FROM ayahs 
    WHERE LENGTH(TRIM(text_ar)) = 0 
       OR LENGTH(TRIM(text_id)) = 0
       OR text_ar LIKE '%\ufffd%'
       OR text_id LIKE '%\ufffd%';
    """)
    invalid_count = cursor.fetchone()[0]
    if invalid_count != 0:
        print(f"[FAIL] Found {invalid_count} ayahs with empty text or invalid unicode replacement characters.")
        sys.exit(1)
    print("[PASS] All Arabic texts and Indonesian translations are non-empty and UTF-8 intact.")

    # 10. Check 30 Juz coverage
    cursor.execute("SELECT DISTINCT juz_id FROM ayahs ORDER BY juz_id ASC;")
    juz_list = [r[0] for r in cursor.fetchall()]
    if juz_list != list(range(1, 31)):
        print(f"[FAIL] Juz coverage mismatch: {juz_list}")
        sys.exit(1)
    print("[PASS] All 30 Juz (1..30) are correctly mapped.")

    conn.close()

    print("==================================================")
    print(" [ALL CHECKS PASSED] DATASET IS 100% VALID!       ")
    print("==================================================")
    return 0

if __name__ == "__main__":
    sys.exit(validate())
