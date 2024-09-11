import os
import sys

commit_log = os.popen(
    "git log --decorate=short --pretty=format:'- [%s](https://github.com/mei-desofs/desofs2024_M1A_2/commit/%h) **by %an on %cd**' --date=format:'%d/%m/%Y at %Hh%M' {}..{}".format(sys.argv[1], sys.argv[2])
).read()

filtered_commits = [line for line in commit_log.split('\n') if line.startswith("- [Merge pull request")]

with open("CHANGELOG.md", "a") as changelog:
    changelog.write("\n ### Release - {}\n #### Contributions\n".format(sys.argv[2]) + "\n".join(filtered_commits))

os.system("git add CHANGELOG.md")
os.system("git commit -m 'update CHANGELOG.md'")
os.system("git push origin")