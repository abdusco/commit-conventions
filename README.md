# CommitConventions

This plugin adds a templated snippet to the commit messages based on branch names in IntelliJ IDEA. It helps developers follow a consistent commit message template when working on projects.

## Installation

To install this plugin in IntelliJ IDEA:
1. Download the plugin from the [Releases](https://github.com/abdusco/commit-conventions/releases) page.
2. Open IntelliJ IDEA and go to `Preferences > Plugins > Install Plugin from Disk`.
3. Select the downloaded plugin file and restart IntelliJ IDEA.

## Features

- Automatically appends "Closes: #XXXX" to commit messages based on the branch name.
- Allows customization of the regex pattern and message template in the plugin settings.
- Preview feature to see how the commit message will look before committing.

## Usage

1. Make changes to your code in IntelliJ IDEA.
2. Commit your changes using Git.
3. The plugin will automatically add "Closes: #XXXX" to your commit message based on the branch name.

## Configuration

You can customize the regex pattern and message template in the plugin settings:
1. Go to `Preferences > Tools > Commit Template Settings`.
2. Enter your desired regex pattern and message template.
3. The preview section will show you a sample commit message based on the current branch name.

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/abdus-dev/commit-conventions/blob/main/LICENSE) file for details.

## Acknowledgements

This plugin was created by [Abdus](https://github.com/abdus-dev) for enhancing the commit message workflow in IntelliJ IDEA.

---
Feel free to add any additional information or instructions specific to the project.
