import sys

TYPE = {"gel": 0, "tablet": 1}
SIZE = {"small": 0, "medium": 1, "large": 2}
CHAR = {0: ".", 1: ":", 2: "|"}

it = iter(l for l in sys.stdin if l.strip())  # skip blank lines
for line in it:
    n = int(line)
    bottles = [tuple(next(it).split()) for _ in range(n)]  # (med, size, type)
    bottles.sort(key=lambda b: (TYPE[b[2]], b[0], SIZE[b[1]]))
    print("".join(CHAR[SIZE[b[1]]] for b in bottles))