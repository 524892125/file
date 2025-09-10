from fontTools import subset
from fontTools.ttLib import TTFont

otf_path = r"D:\Download\SourceHanSansCN-Bold.otf"
woff2_path = "output.woff2"

options = subset.Options()
options.flavor = "woff2"
options.ignore_missing_glyphs = True

# 加载字体
font = TTFont(otf_path)

# 获取字体所有支持的字符
all_unicodes = []
for cmap in font['cmap'].tables:
    all_unicodes.extend(cmap.cmap.keys())
all_unicodes = set(all_unicodes)

# 过滤掉 emoji 区间 U+1F000–1FFFF
filtered_unicodes = [u for u in all_unicodes if not (0x1F000 <= u <= 0x1FFFF)]

# 转换为子集
font = subset.load_font(otf_path, options)
subsetter = subset.Subsetter(options)
subsetter.populate(unicodes=filtered_unicodes)
subsetter.subset(font)

subset.save_font(font, woff2_path, options)
print("转换完成（已去除 Emoji）:", woff2_path)
