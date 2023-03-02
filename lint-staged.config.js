module.exports = {
  "*.{js,jsx,ts,tsx}": ["eslint --fix", "eslint"],
  "**/*.ts?(x)": () => "npm run check-types",
  "*.json": ["prettier --write"],
  "*.java": ['prettier --write "**/*.java'],
  "*.properties": ['prettier --write "**/*.properties"'],
};
