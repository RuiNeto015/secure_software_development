import json
import os
import sys

from tabulate import tabulate

with open('.secrets.baseline', 'r') as file1:
    secretsV1 = json.load(file1)

os.system("detect-secrets scan --baseline .secrets.baseline")      

with open('.secrets.baseline', 'r') as file2:
    secretsV2 = json.load(file2)

def get_secrets(results):
    secrets = {}
    for filename, secrets_list in results.items():
        for secret in secrets_list:
            secret_key = (filename, secret['type'], secret['line_number'])
            secrets[secret_key] = secret['hashed_secret']
    return secrets

def create_html_table(data, headers):
    html_content = "<html><head><title>New Secrets Found</title></head><body>"
    html_content += "<h1>New secrets found:</h1>"
    html_content += "<table border='1'>"
    html_content += "<tr>" + "".join(f"<th>{header}</th>" for header in headers) + "</tr>"
    for row in data:
        html_content += "<tr>" + "".join(f"<td>{cell}</td>" for cell in row) + "</tr>"
    html_content += "</table>"
    html_content += "</body></html>"
    return html_content

secretsV1_set = get_secrets(secretsV1['results'])
secretsV2_set = get_secrets(secretsV2['results'])

new_secrets = []
for key, hashed_secret in secretsV2_set.items():
    if key not in secretsV1_set:
        new_secrets.append((key[0], key[1], key[2], hashed_secret))

if new_secrets:
    print("New secrets found:")
    headers = ["File", "Type", "Line", "Hashed Secret"]
    print(tabulate(new_secrets, headers=headers, tablefmt="grid"))

    html_content = create_html_table(new_secrets, headers)
    with open("new_secrets.html", "w") as html_file:
        html_file.write(html_content)

    print("Use 'detect-secrets audit .secrets.baseline' to audit the baseline ...\n\n")
    print("Commiting new .secrets.baseline ...\n")
    os.system("git config push.default current")
    os.system("git add .secrets.baseline")
    os.system("git commit -m '.secrets.baseline'")
    os.system("git checkout -b {}".format(sys.argv[1]))
    os.system("git remote set-url origin git@github.com:mei-desofs/desofs2024_M1A_2.git")
    os.system("git push origin")
    sys.exit(1)
else:
    print("No new secrets detected.")
    sys.exit(0)