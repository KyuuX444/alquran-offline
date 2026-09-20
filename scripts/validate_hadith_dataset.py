#!/usr/bin/env python3
"""
Independent Hadith Dataset Validation Tool for Al-Qur'an Offline
Enforces integrity of the 42 Hadits Arba'in An-Nawawi in SQLite and JSON.
"""

import hashlib
import json
import os
import sqlite3
import sys

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets")
HADITHS_JSON_PATH = os.path.join(ASSETS_DIR, "hadith", "hadiths.json")
METADATA_PATH = os.path.join(ASSETS_DIR, "hadith_metadata.json")
DB_PATH = os.path.join(ASSETS_DIR, "quran.db")

def validate():
    print("==================================================")
    print("       HADITH DATASET INTEGRITY VALIDATOR         ")
    print("==================================================")

    if not os.path.exists(HADITHS_JSON_PATH):
        print(f"[FAIL] Hadith JSON file not found: {HADITHS_JSON_PATH}")
        sys.exit(1)

    if not os.path.exists(METADATA_PATH):
        print(f"[FAIL] Hadith metadata file not found: {METADATA_PATH}")
        sys.exit(1)

    # 1. Verify JSON and Checksum
    with open(HADITHS_JSON_PATH, "rb") as f:
        actual_hash = hashlib.sha256(f.read()).hexdigest()

    with open(METADATA_PATH, "r", encoding="utf-8") as f:
        meta = json.load(f)

    if meta.get("sha256_checksum") != actual_hash:
        print(f"[FAIL] SHA-256 Checksum mismatch for Hadith JSON!")
        print(f"  Expected: {meta.get('sha256_checksum')}")
        print(f"  Actual:   {actual_hash}")
        sys.exit(1)
    print(f"[PASS] Hadith checksum verified ({actual_hash[:16]}...)")

    with open(HADITHS_JSON_PATH, "r", encoding="utf-8") as f:
        hadiths = json.load(f)

    if len(hadiths) != 42:
        print(f"[FAIL] Expected 42 hadiths, got {len(hadiths)}")
        sys.exit(1)
    print("[PASS] 42 Hadiths present in JSON.")

    # 2. Check each Hadith item
    for idx, h in enumerate(hadiths, start=1):
        if h["id"] != idx or h["nomor"] != idx:
            print(f"[FAIL] Non-sequential Hadith ID or number: expected {idx}, found id={h['id']}, nomor={h['nomor']}")
            sys.exit(1)
        if not h.get("judul", "").strip():
            print(f"[FAIL] Empty title in Hadith #{idx}")
            sys.exit(1)
        if not h.get("teks_ar", "").strip():
            print(f"[FAIL] Empty Arabic text in Hadith #{idx}")
            sys.exit(1)
        if not h.get("teks_id", "").strip():
            print(f"[FAIL] Empty Indonesian text in Hadith #{idx}")
            sys.exit(1)
        if not h.get("sumber", "").strip():
            print(f"[FAIL] Empty source in Hadith #{idx}")
            sys.exit(1)
    print("[PASS] All 42 Hadiths have valid title, Arabic text, translation, and source.")

    # 3. Verify SQLite DB Table
    if os.path.exists(DB_PATH):
        conn = sqlite3.connect(DB_PATH)
        cursor = conn.cursor()
        cursor.execute("SELECT COUNT(*) FROM hadiths;")
        db_count = cursor.fetchone()[0]
        if db_count != 42:
            print(f"[FAIL] Database has {db_count} hadiths, expected 42")
            sys.exit(1)
        conn.close()
        print("[PASS] Database 'hadiths' table contains 42 verified rows.")

    print("==================================================")
    print(" [ALL CHECKS PASSED] HADITH DATASET IS 100% VALID ")
    print("==================================================")
    return 0

if __name__ == "__main__":
    sys.exit(validate())
