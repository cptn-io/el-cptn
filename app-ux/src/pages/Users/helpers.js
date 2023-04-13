function capitalize(val) {
    if (typeof val !== 'string' || val.length === 0) return '';

    return val[0].toUpperCase() + val.slice(1);
}

function extractNameFromEmail(email) {
    const parts = email?.split('@')?.[0]?.split('.');
    const firstName = capitalize(parts?.[0] || '');
    const lastName = capitalize(parts?.[1] || '');
    return { firstName, lastName };
}

export { extractNameFromEmail }