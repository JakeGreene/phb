declare -a races=("Dwarf" "Elf" "Halfing" "Human" "Dragonborn" "Gnome" "Half-Elf" "Half-Orc" "Tiefling")
host=localhost
port=9000

for r in "${races[@]}" 
do 
  curl -X POST -H "Content-Type: application/json" -d '{"name":"'"$r"'"}' "${host}":"${port}"/races
done
