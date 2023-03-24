/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      maxWidth: {
        '1/4': '25%',
        '1/2': '50%',
        '3/5': '60%',
        '3/4': '75%',
      }
    },
  },
  plugins: [require("@tailwindcss/typography"), require("daisyui")]
}