import re, os, json, urllib.request, time, sys
SCRATCH="/private/tmp/claude-501/-Users-cambroise-Documents-MyLife-codes-heartsutra/db14ed39-9139-4b3c-a196-1f85ed1bfe02/scratchpad/kanjivg_svg"
os.makedirs(SCRATCH, exist_ok=True)
kt=open("/Users/cambroise/Documents/MyLife/codes/heartsutra/app/src/main/java/cloud/ambroise/heartsutra/data/SutraData.kt",encoding="utf-8").read()
origs=re.findall(r'Segment\(\d+,\s*"([^"]+)"', kt)
chars=[]
seen=set()
for o in origs:
    for ch in o:
        if '㐀'<=ch<='鿿' and ch not in seen:
            seen.add(ch); chars.append(ch)
print("kanji:",len(chars))
BASE="https://raw.githubusercontent.com/KanjiVG/kanjivg/master/kanji/%05x.svg"
bundle={}
missing=[]
for ch in chars:
    cp="%05x"%ord(ch)
    fp=os.path.join(SCRATCH,cp+".svg")
    if not os.path.exists(fp) or os.path.getsize(fp)<200:
        try:
            req=urllib.request.Request(BASE%ord(ch),headers={"User-Agent":"heartsutra-build"})
            data=urllib.request.urlopen(req,timeout=25).read()
            open(fp,"wb").write(data)
            time.sleep(0.05)
        except Exception as e:
            missing.append(ch); print("MISS",ch,cp,e); continue
    svg=open(fp,encoding="utf-8").read()
    # ordered stroke path d-attrs (KanjiVG lists them in stroke order)
    ds=re.findall(r'<path[^>]*\bd="([^"]+)"', svg)
    if ds:
        bundle[ch]=ds
    else:
        missing.append(ch)
print("bundled:",len(bundle),"missing:",missing)
out="/Users/cambroise/Documents/MyLife/codes/heartsutra/app/src/main/assets/kanjivg/strokes.json"
os.makedirs(os.path.dirname(out),exist_ok=True)
json.dump(bundle, open(out,"w",encoding="utf-8"), ensure_ascii=False, separators=(",",":"))
print("wrote",out,os.path.getsize(out),"bytes")
