declare -a races=("Dwarf" "Elf" "Halfing" "Human" "Dragonborn" "Gnome" "Half-Elf" "Half-Orc" "Tiefling")
host=http://player-handbook.herokuapp.com
#port=9000

#for r in "${races[@]}" 
#do 
#  curl -X POST -H "Content-Type: application/json" -d '{"name":"'"$r"'"}' "${host}":"${port}"/races
#done
for r in "${races[@]}" 
do 
  curl -X POST -H "Content-Type: application/json" -d '{"name":"'"$r"'"}' "${host}"/races
done
