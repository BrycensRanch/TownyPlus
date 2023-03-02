# Rename all *.txt to *.md
for file in *.java; do
    mv -- "$file" "${file%.java}.kt"
done
