export function isNotEmpty(s: string | null): boolean {
    return s != null && s.length > 0;
}

export function isEmpty(s: string): boolean {
    return !isNotEmpty(s)
}