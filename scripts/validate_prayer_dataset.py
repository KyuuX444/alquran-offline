#!/usr/bin/env python3
"""
Validates Bacaan Sholat dataset integrity, structure, non-empty Arabic text,
transliteration, translation, and authentic source citations.
"""
import json
import os
import sys

def main():
    dataset_path = "app/src/main/assets/prayer/prayer_readings.json"
    if not os.path.exists(dataset_path):
        print(f"[FAIL] Dataset file not found: {dataset_path}")
        sys.exit(1)

    with open(dataset_path, "r", encoding="utf-8") as f:
        readings = json.load(f)

    if not isinstance(readings, list) or len(readings) == 0:
        print("[FAIL] Readings dataset must be a non-empty JSON array")
        sys.exit(1)

    seen_ids = set()
    required_fields = ["id", "title", "category", "arabic", "transliteration", "translation", "source"]

    for idx, item in enumerate(readings):
        for field in required_fields:
            if field not in item or not str(item[field]).strip():
                print(f"[FAIL] Item index {idx} missing or has empty field: {field}")
                sys.exit(1)

        item_id = item["id"]
        if item_id in seen_ids:
            print(f"[FAIL] Duplicate item id: {item_id}")
            sys.exit(1)
        seen_ids.add(item_id)

        # Verify authentic source citation format (Bukhari, Muslim, Abu Daud, Tirmidzi, etc.)
        src = item["source"].lower()
        if not ("bukhari" in src or "muslim" in src or "daud" in src or "tirmidzi" in src or "ibnu majah" in src or "qs." in src or "qur'an" in src):
            print(f"[FAIL] Item id {item_id} ({item['title']}) lacks verified primary source: {item['source']}")
            sys.exit(1)

    print(f"[PASS] All {len(readings)} Bacaan Sholat entries verified successfully with authentic sources!")

if __name__ == "__main__":
    main()
