if ([System.Diagnostics.EventLog]::SourceExists($args[0]) -eq $false) {
    [System.Diagnostics.EventLog]::CreateEventSource($args[0], "Application")
}
write-eventlog -logname Application -source $args[0] -eventID 1 -entrytype Information -message $args[1]
