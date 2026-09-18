
function Get-GitHubIssueStory {
   param (
     [Parameter(Mandatory = $true)]
     [int]$IssueNumber
   )

   $issue = Invoke-RestMethod `
      -Uri "https://api.github.com/repos/jotalubango/java-database-capstone/issues/$IssueNumber"

   # Replace invalid filename characters
   $safeTitle = $issue.title `
   -replace '[<>:"/\\|?*]', '_' `
   -replace '\s+', '_'

   $outputFile = "${safeTitle}_$IssueNumber.md"
#    $outputFile = "$IssueNumber_${safeTitle}.md"

   $issue.body | Set-Content $outputFile

   Write-Host "Created $outputFile"
}

for ($i = 1; $i -le 17; $i++) {
Get-GitHubIssueStory $i
}
