#!/usr/bin/env python3
"""
Generate all Android icon densities and legacy/adaptive assets from source icon.
"""

import os
import subprocess

SRC = "/root/Alquran/app/src/main/res/drawable/app_icon_source.jpg"
RES_DIR = "/root/Alquran/app/src/main/res"

DENSITIES = {
    "mipmap-mdpi": (48, 108, 72),
    "mipmap-hdpi": (72, 162, 108),
    "mipmap-xhdpi": (96, 216, 144),
    "mipmap-xxhdpi": (144, 324, 216),
    "mipmap-xxxhdpi": (192, 432, 288),
}

def generate():
    for folder, (square_sz, canvas_sz, inner_sz) in DENSITIES.items():
        dir_path = os.path.join(RES_DIR, folder)
        os.makedirs(dir_path, exist_ok=True)

        # 1. Legacy Square Icon (ic_launcher.png)
        square_path = os.path.join(dir_path, "ic_launcher.png")
        subprocess.run([
            "convert", SRC,
            "-resize", f"{square_sz}x{square_sz}^",
            "-gravity", "center",
            "-extent", f"{square_sz}x{square_sz}",
            square_path
        ], check=True)

        # 2. Legacy Round Icon (ic_launcher_round.png)
        round_path = os.path.join(dir_path, "ic_launcher_round.png")
        r = square_sz // 2
        subprocess.run([
            "convert", square_path,
            "(", "-size", f"{square_sz}x{square_sz}", "xc:none",
            "-fill", "white",
            "-draw", f"circle {r},{r} {r},1", ")",
            "-compose", "DstIn", "-composite",
            round_path
        ], check=True)

        # 3. Adaptive Foreground Icon (ic_launcher_foreground.png)
        # Inner 72dp icon placed on 108dp transparent canvas
        fg_path = os.path.join(dir_path, "ic_launcher_foreground.png")
        subprocess.run([
            "convert", SRC,
            "-resize", f"{inner_sz}x{inner_sz}^",
            "-gravity", "center",
            "-extent", f"{inner_sz}x{inner_sz}",
            "(", "-size", f"{inner_sz}x{inner_sz}", "xc:none",
            "-fill", "white",
            "-draw", f"circle {inner_sz//2},{inner_sz//2} {inner_sz//2},1", ")",
            "-compose", "DstIn", "-composite",
            "-gravity", "center",
            "-background", "none",
            "-extent", f"{canvas_sz}x{canvas_sz}",
            fg_path
        ], check=True)

        print(f"Generated icons for {folder}: square={square_sz}x{square_sz}, round={square_sz}x{square_sz}, fg={canvas_sz}x{canvas_sz}")

    # Also generate high-res 512x512 icon in drawable
    subprocess.run([
        "convert", SRC,
        "-resize", "512x512^",
        "-gravity", "center",
        "-extent", "512x512",
        os.path.join(RES_DIR, "drawable", "ic_launcher_playstore.png")
    ], check=True)
    print("Generated 512x512 playstore icon.")

if __name__ == "__main__":
    generate()
