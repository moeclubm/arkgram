"""Unified renderer for the new ArkGram boat logo (Design B).

Single source of truth for the boat shape; regenerates every PNG asset and the
vector drawable from these normalized coordinates.
"""
from PIL import Image, ImageDraw
import os

REPO = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
RES = os.path.join(REPO, "TMessagesProj", "src", "main", "res")
RES_STANDALONE = os.path.join(REPO, "TMessagesProj_AppStandalone", "src", "main", "res")

INK = (47, 42, 36, 255)   # #2F2A24 — same dark warm brown the app already uses
CREAM = (244, 226, 184, 255)  # #F4E2B8 — current launcher background
WHITE = (255, 255, 255, 255)

# Design B — normalized coordinates (multiplied by scale `s` and offset by (cx,cy)).
# Tall left main sail, shorter right jib, wide flat hull.
SAIL_MAIN = [(-0.02, -0.40), (-0.02,  0.10), (-0.36,  0.10)]
SAIL_JIB  = [( 0.04, -0.28), ( 0.36,  0.10), ( 0.04,  0.10)]
HULL      = [(-0.46,  0.14), ( 0.46,  0.14), ( 0.30,  0.42), (-0.30,  0.42)]

DENSITIES = ["mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"]
SCALE = {"mdpi": 1, "hdpi": 1.5, "xhdpi": 2, "xxhdpi": 3, "xxxhdpi": 4}


def draw_boat(draw: ImageDraw.ImageDraw, cx: float, cy: float, s: float, color):
    """Stamp Design B at (cx,cy) with overall scale s, in solid `color`."""
    for poly in (SAIL_MAIN, SAIL_JIB, HULL):
        pts = [(cx + dx * s, cy + dy * s) for dx, dy in poly]
        draw.polygon(pts, fill=color)


def render_transparent(w: int, h: int, color, fill_frac: float = 0.58) -> Image.Image:
    """Boat in `color` on transparent canvas. `fill_frac` controls the boat's
    width relative to the canvas (so design width 0.92*s ≈ fill_frac*min(w,h))."""
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    s = min(w, h) * fill_frac / 0.92  # boat width = fill_frac * min(w,h)
    draw_boat(d, w / 2, h / 2, s, color)
    return img


def render_launcher(size: int, *, round_corners: bool = True,
                    bg=CREAM, ink=INK, fill_frac: float = 0.54) -> Image.Image:
    """Rounded-square (or fully round) launcher: bg + boat."""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    if round_corners:
        r = int(size * 0.22)
        d.rounded_rectangle([0, 0, size - 1, size - 1], radius=r, fill=bg)
    else:
        d.ellipse([0, 0, size - 1, size - 1], fill=bg)
    s = size * fill_frac / 0.92
    draw_boat(d, size / 2, size / 2, s, ink)
    return img


def save(img: Image.Image, path: str):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path, optimize=True)


# ---- targeted regenerators ----

def regen_icon_foreground():
    """Adaptive-icon foreground layer: dark boat on transparent, 108dp safe zone.
    Densities: mdpi=108, hdpi=162, xhdpi=216, xxhdpi=324, xxxhdpi=432.
    Variants: {,_round,_sa}. Same boat shape for every variant — variants live
    in the background drawables."""
    base = 108
    names = ["icon_foreground", "icon_foreground_round", "icon_foreground_sa",
             "icon_3_foreground", "icon_3_foreground_round", "icon_3_foreground_sa",
             "icon_5_foreground", "icon_5_foreground_round", "icon_5_foreground_sa",
             "icon_6_foreground", "icon_6_foreground_round", "icon_6_foreground_sa"]
    written = 0
    for d in DENSITIES:
        size = int(base * SCALE[d])
        img = render_transparent(size, size, INK, fill_frac=0.58)
        for n in names:
            path = os.path.join(RES, f"mipmap-{d}", f"{n}.png")
            if os.path.exists(path):
                save(img, path)
                written += 1
    return written


def regen_notification():
    """Status-bar notification icon: WHITE silhouette on transparent.
    Densities: mdpi=24, hdpi=36, xhdpi=48, xxhdpi=72, xxxhdpi=96."""
    base = 24
    written = 0
    for d in DENSITIES:
        size = int(base * SCALE[d])
        img = render_transparent(size, size, WHITE, fill_frac=0.80)
        path = os.path.join(RES, f"drawable-{d}", "notification.png")
        if os.path.exists(path):
            save(img, path)
            written += 1
    return written


def regen_launchers():
    """Pre-composited launcher PNGs (Android < 8 fallback):
    cream rounded square + dark boat.
    Densities: mdpi=48, hdpi=72, xhdpi=96, xxhdpi=144, xxxhdpi=192."""
    base = 48
    written = 0
    targets = [
        ("ic_launcher",     "mipmap"),
        ("ic_launcher_round","mipmap"),
        ("ic_launcher_sa",  "mipmap"),
        ("icon_2_launcher", "mipmap"),
        ("icon_2_launcher_round","mipmap"),
        ("icon_3_launcher", "mipmap"),
        ("icon_3_launcher_round","mipmap"),
        ("icon_4_launcher", "mipmap"),
        ("icon_4_launcher_round","mipmap"),
        ("icon_5_launcher", "mipmap"),
        ("icon_5_launcher_round","mipmap"),
        ("icon_6_launcher", "mipmap"),
        ("icon_6_launcher_round","mipmap"),
        ("ic_launcher_dr",  "drawable"),
    ]
    standalone_names = ["ic_launcher_sa", "icon_2_launcher_sa", "icon_3_launcher_sa",
                        "icon_4_launcher_sa", "icon_5_launcher_sa", "icon_6_launcher_sa"]
    for d in DENSITIES:
        size = int(base * SCALE[d])
        sq = render_launcher(size, round_corners=True)
        for name, sub in targets:
            path = os.path.join(RES, f"{sub}-{d}", f"{name}.png")
            if os.path.exists(path):
                save(sq, path)
                written += 1
        for name in standalone_names:
            sa_path = os.path.join(RES_STANDALONE, f"mipmap-{d}", f"{name}.png")
            if os.path.exists(sa_path):
                save(sq, sa_path)
                written += 1
    return written


def regen_intro_plane():
    """Boat texture for the OpenGL intro animation. Loaded with tint #FF2F2A24
    in IntroActivity.java:789 — we just need the alpha silhouette.
    Densities: mdpi=82x74, hdpi=123x111, xhdpi=164x148, xxhdpi=246x222.
    No xxxhdpi (matches existing setup)."""
    base_w, base_h = 82, 74
    written = 0
    for d in ["mdpi", "hdpi", "xhdpi", "xxhdpi"]:
        w = int(base_w * SCALE[d])
        h = int(base_h * SCALE[d])
        # Use white because the texture is loaded with an explicit color in code;
        # only alpha matters. White preserves alpha cleanly across resamplers.
        img = render_transparent(w, h, WHITE, fill_frac=0.92)
        path = os.path.join(RES, f"drawable-{d}", "intro_tg_plane.png")
        if os.path.exists(path):
            save(img, path)
            written += 1
    return written


def regen_all():
    n1 = regen_icon_foreground()
    n2 = regen_notification()
    n3 = regen_launchers()
    n4 = regen_intro_plane()
    print(f"icon_foreground:  {n1} files")
    print(f"notification:     {n2} files")
    print(f"launchers:        {n3} files")
    print(f"intro plane:      {n4} files")
    print(f"total:            {n1+n2+n3+n4} files")


if __name__ == "__main__":
    regen_all()
