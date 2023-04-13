const {
    pbkdf2Sync,
    createDecipheriv,
} = require('crypto');

const ALGORITHM = 'aes-256-gcm';
const TAG_LENGTH = 16;
const IV_LENGTH = 16;

const secretKey = process.env.CPTN_CRYPTO_SECRET;
const salt = process.env.CPTN_CRYPTO_SALT;

const key = pbkdf2Sync(secretKey, salt, 1024, 32, "sha1")

function decrypt(cipherText) {

    let cipherBytesWithIV = Buffer.from(cipherText, 'base64');
    const iv = cipherBytesWithIV.subarray(0, IV_LENGTH);
    const tag = cipherBytesWithIV.subarray(-TAG_LENGTH);

    const cipherBytes = cipherBytesWithIV.subarray(IV_LENGTH, cipherBytesWithIV.length - TAG_LENGTH);
    const decipher = createDecipheriv(ALGORITHM, key, iv);
    decipher.setAuthTag(tag);

    let decrypted = decipher.update(cipherBytes, null, 'utf8');
    decrypted += decipher.final('utf8');
    return decrypted;
}

module.exports = {
    decrypt
}