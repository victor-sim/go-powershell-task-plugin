# GoCD PowerShell script execution plug-in

A [GoCD](https://www.go.cd) plugin that executes PowerShell Script

Introduction
------------
This is a [task plug-in](https://plugin-api.gocd.io/current/tasks/#task-plugins) plugin for [GoCD](https://www.go.cd)
 to execute PowerShell script on GoCD agent

Installation
------------
Just drop [task.powershell.(version).jar](https://github.com/varchev/go-generic-artifactory-poller/releases) into plugins/external 
directory and 
restart GoCD. More details [here](https://docs.go.cd/current/extension_points/plugin_user_guide.html)

![Task Configuration][1]

Error Action Preference
---------------------

Define error action preference for script execution.
It will inject following script into head of script content:

    $script:ErrorActionPreference = [System.Management.Automation.ActionPreference]::<Stop/SilentlyContinue/Continue/Ignore...>

Script
------------------
Content of script to execute. Content in this text area will be created as temporary PowerShell script file, and then be executed.


CPU Architecture
-------------------------------
CPU Architecture to run Powershell command

[1]: doc/pstask.PNG "Configure task"

