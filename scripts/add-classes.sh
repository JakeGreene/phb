declare -a classes=("Barbarian" "Bard" "Cleric" "Druid" "Fighter" "Monk" "Paladin" "Ranger" "Rogue" "Sorcerer" "Warlock" "Wizard")
host=http://player-handbook.herokuapp.com
#port=9000

#for c in "${classes[@]}" 
#do 
#  curl -X POST -H "Content-Type: application/json" -d '{"name":"'"$c"'"}' "${host}":"${port}"/classes
#done
for c in "${classes[@]}" 
do 
  curl -X POST -H "Content-Type: application/json" -d '{"name":"'"$c"'"}' "${host}"/classes
done
