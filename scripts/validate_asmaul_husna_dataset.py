#!/usr/bin/env python3
"""
Integrity Validator for 99 Asmaul Husna Dataset
Verifies that all 99 names of Allah are present, sequential (1..99),
with authentic vocalized Arabic text, Indonesian Latin transliteration, and Indonesian meaning.
"""

import hashlib
import json
import os
import sys

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "asmaul_husna")
JSON_PATH = os.path.join(ASSETS_DIR, "asmaul_husna.json")
METADATA_PATH = os.path.join(ASSETS_DIR, "asmaul_husna_metadata.json")

def validate():
    if not os.path.exists(JSON_PATH):
        print(f"[FAIL] Asmaul Husna JSON not found: {JSON_PATH}")
        sys.exit(1)
    if not os.path.exists(METADATA_PATH):
        print(f"[FAIL] Asmaul Husna metadata not found: {METADATA_PATH}")
        sys.exit(1)

    with open(METADATA_PATH, "r", encoding="utf-8") as f:
        meta = json.load(f)

    with open(JSON_PATH, "rb") as f:
        actual_hash = hashlib.sha256(f.read()).hexdigest()

    expected_hash = meta.get("sha256_checksum")
    if actual_hash != expected_hash:
        print(f"[FAIL] SHA-256 mismatch! Expected {expected_hash}, got {actual_hash}")
        sys.exit(1)

    with open(JSON_PATH, "r", encoding="utf-8") as f:
        items = json.load(f)

    if len(items) != 99:
        print(f"[FAIL] Expected 99 items, found {len(items)}")
        sys.exit(1)

    for idx, item in enumerate(items, start=1):
        if item.get("id") != idx:
            print(f"[FAIL] Non-sequential id at position {idx}: {item.get('id')}")
            sys.exit(1)
        name_ar = item.get("nameAr", "").strip()
        name_latin = item.get("nameLatin", "").strip()
        meaning_id = item.get("meaningId", "").strip()

        if not name_ar or not name_latin or not meaning_id:
            print(f"[FAIL] Empty field at item {idx}: {item}")
            sys.exit(1)

    print(f"[PASS] All 99 Asmaul Husna verified successfully! (SHA-256: {actual_hash[:16]}...)")
    return 0

if __name__ == "__main__":
    sys.exit(validate())
