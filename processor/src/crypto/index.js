const {
    pbkdf2Sync,
    createDecipheriv,
} = require('crypto');

const ALGORITHM = 'aes-256-gcm';
const SALT_LENGTH = 16;
const TAG_LENGTH = 16;
const IV_LENGTH = 16;

const secretKey = process.env.CPTN_CRYPTO_SECRET;


function decrypt(cipherText) {

    let cipherBytesWithIVAndSalt = Buffer.from(cipherText, 'base64');
    const salt = cipherBytesWithIVAndSalt.subarray(0, SALT_LENGTH);
    const iv = cipherBytesWithIVAndSalt.subarray(SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
    const tag = cipherBytesWithIVAndSalt.subarray(-TAG_LENGTH);

    const key = pbkdf2Sync(secretKey, salt, 1024, 32, "sha1")

    const cipherBytes = cipherBytesWithIVAndSalt.subarray(SALT_LENGTH + IV_LENGTH, cipherBytesWithIVAndSalt.length - TAG_LENGTH);
    const decipher = createDecipheriv(ALGORITHM, key, iv);
    decipher.setAuthTag(tag);

    let decrypted = decipher.update(cipherBytes, null, 'utf8');
    decrypted += decipher.final('utf8');
    return decrypted;
}

module.exports = {
    decrypt
}