const crypto = require("crypto");

const ALGORITHM = 'aes-256-gcm';
const TAG_LENGTH = 16;
const IV_LENGTH = 12;

const secretKey = Buffer.from(process.env.DB_CRYPTO_SECRET, 'hex');

function decrypt(cipherText) {
    const cipherBytesWithIV = Buffer.from(cipherText, 'base64');

    const iv = cipherBytesWithIV.subarray(0, IV_LENGTH);
    const tag = cipherBytesWithIV.subarray(-TAG_LENGTH);

    const cipherBytes = cipherBytesWithIV.subarray(IV_LENGTH, cipherBytesWithIV.length - TAG_LENGTH);

    const decipher = crypto.createDecipheriv(ALGORITHM, secretKey, iv, { authTagLength: TAG_LENGTH });
    decipher.setAuthTag(tag);

    let decrypted = decipher.update(cipherBytes, null, 'utf8');
    decrypted += decipher.final('utf8');
    return decrypted;
}

module.exports = {
    decrypt
}