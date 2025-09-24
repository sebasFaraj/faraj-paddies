#!/usr/bin/env python3
import sys

# use memoization. Base case: cycle_len(1) = 1.
memo = {1: 1}

def cycle_len(n: int) -> int:
    original = n
    stack = []
    while n not in memo:
        stack.append(n)
        if n % 2 == 1:           # odd
            n = 3 * n + 1
        else:               # even
            n = n // 2         
    # go backwards and fill the memo
    base = memo[n]
    for k, x in enumerate(reversed(stack), start=1):
        memo[x] = base + k
    return memo[original]

def solve_pair(i: int, j: int):
    lo, hi = (i, j) if i <= j else (j, i)
    max_len = -1
    best_n = lo
    # iterate through the range and find the max cycle length
    for n in range(lo, hi + 1):
        curr = cycle_len(n)
        if curr > max_len or (curr == max_len and n < best_n):
            max_len = curr
            best_n = n
    # preserve original input order
    print(i, j, best_n, max_len)

def main():
    for line in sys.stdin:
        line = line.strip()
        if not line:
            continue
        parts = line.split()
        if len(parts) != 2:
            continue
        i, j = map(int, parts)
        solve_pair(i, j)

if __name__ == "__main__":
    main()
