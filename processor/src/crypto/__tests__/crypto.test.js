process.env.CPTN_CRYPTO_SECRET = 'secret';

const { decrypt } = require('..');

describe('decrypt', () => {

    test('should decrypt cipher text', () => {
        const cipherText = 'jJhIyGoZG+mEv/DMC4AWuERHRs5oTYpqAsnn0Dr6RInRnIBN/ijJZkMlHQmzaFAe40Yo'; //bar

        const decrypted = decrypt(cipherText);
        expect(decrypted).toEqual('bar');
    });
});