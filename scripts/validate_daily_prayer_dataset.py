#!/usr/bin/env python3
"""
Validates the integrity, checksum, and authentic citations of the Doa Harian dataset.
"""

import os
import sys
import json
import hashlib

DATA_PATH = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "daily_prayer", "daily_prayers.json")
META_PATH = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets", "daily_prayer", "daily_prayer_metadata.json")

def validate():
    if not os.path.exists(DATA_PATH):
        print(f"[FAIL] Missing dataset file: {DATA_PATH}")
        sys.exit(1)

    if not os.path.exists(META_PATH):
        print(f"[FAIL] Missing metadata file: {META_PATH}")
        sys.exit(1)

    with open(DATA_PATH, "rb") as f:
        content = f.read()
        sha = hashlib.sha256(content).hexdigest()

    with open(META_PATH, "r", encoding="utf-8") as f:
        meta = json.load(f)

    if meta.get("sha256") != sha:
        print(f"[FAIL] Checksum mismatch! Expected {meta.get('sha256')}, got {sha}")
        sys.exit(1)

    with open(DATA_PATH, "r", encoding="utf-8") as f:
        prayers = json.load(f)

    if len(prayers) < 30:
        print(f"[FAIL] Expected at least 30 daily prayers, found {len(prayers)}")
        sys.exit(1)

    seen_ids = set()
    for item in prayers:
        pid = item.get("id")
        if not pid or pid in seen_ids:
            print(f"[FAIL] Invalid or duplicate prayer id: {pid}")
            sys.exit(1)
        seen_ids.add(pid)

        for field in ["title", "category", "arabic", "transliteration", "translation", "source", "reference"]:
            val = item.get(field, "")
            if not val or not val.strip():
                print(f"[FAIL] Empty field '{field}' in prayer id {pid}")
                sys.exit(1)

        source = item.get("source", "").lower()
        valid = any(kw in source for kw in ["bukhari", "muslim", "abu daud", "tirmidzi", "ibnu majah", "malik", "ahmad", "ibnu hibban", "qs."])
        if not valid:
            print(f"[FAIL] Unverified source for '{item.get('title')}': {source}")
            sys.exit(1)

    print(f"[PASS] All {len(prayers)} Doa Harian verified successfully with authentic sources! (SHA-256: {sha[:16]}...)")

if __name__ == "__main__":
    validate()
