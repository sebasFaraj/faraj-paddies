import sys

def longest_distinct_subsequence(words):
    last = {}           # fruit: last seen index
    start = 0           # window start
    best_len = 0
    best_l = 0
    best_r = -1

    for i, w in enumerate(words):
        if w in last and last[w] >= start:
            # duplicate found. Move start right after last occurrence
            start = last[w] + 1
        last[w] = i

        cur_len = i - start + 1
        if cur_len > best_len:   # want to keep the first in case of ties
            best_len = cur_len
            best_l, best_r = start, i

    return words[best_l:best_r+1]

def main():
    case_lines = [line.strip() for line in sys.stdin if line.strip()]

    for line in case_lines:
        fruits = line.split()
        best = longest_distinct_subsequence(fruits)
        print(f"{len(best)}: {', '.join(best)}")

if __name__ == "__main__":
    main()
