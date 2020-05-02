CALL conda activate py36env
for /l %%n in (1,1,2) do (
  CALL echo %%n
  CALL java -jar InfiniteMarioPlayer.jar %%n
  CALL py InfiniteMarioTrainer.py
)
CALL pause