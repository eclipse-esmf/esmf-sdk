# Contribution Guideline ESMF SDK

Thank you for your interest in contributing to the ESMF SDK. Use this repository to contribute to
the SDK as easy and transparent as possible, whether it is:

* Reporting a bug
* Submitting a fix
* Proposing new features
* something else

The ESMF SDK is developed in the context of the [Eclipse Semantic Modeling
Framework](https://projects.eclipse.org/projects/dt.esmf/). It is based on the Semantic Aspect Meta
Model (SAMM) and supports its use.

# Contributing Source Code (using GitHub)

* We use this GitHub repository to track issues and feature requests.
* For general discussions of the ESMF, modeling questions etc. we use the [ESMF Chat](https://chat.eclipse.org/#/room/#eclipse-semantic-modeling-framework:matrix.eclipse.org).
* For discussions specific to development, the preferred way is the [developer mailing list](https://accounts.eclipse.org/mailing-list/esmf-dev).

## Architecture Decision Records

Decisions on code and architecture are documented using [Markdown Any Decision
Records](https://adr.github.io/madr/) in [documentation/decisions/](https://github.com/eclipse-esmf/esmf-sdk/tree/main/documentation/decisions).

## Branching
We follow the [Git branching guidance](https://docs.microsoft.com/en-us/azure/devops/repos/git/git-branching-guidance?view=azure-devops).

More specifically the repository has the following branches:

name of branch | description
----| ----
`main` | Contains the latest state of the repository
`v{version_number}-RC{rc_number}` | A "release candidate": A version that freezes major features and
can be considered a pre-release of the next full release. 
`v{version_number}` | A full release of the respective version.
`feature/#{issue_number}-{feature_name}` | Contains the development on a specific feature and is
intended to be merged back into the `main` branch as soon as possible. Note, that it is recommended
for contributors to create and develop feature branches in a personal fork and not the upstream
repository.
`bug/#{issue_number}-{bug_name}` | Contains the development of (usually smaller) changes in files of
the repository that do not introduce new functionality but fix mistakes, errors or inconsistencies.
These branches should be merged back into the `main`branch as soon as possible.

## Issues
We use the `Issues` feature of GitHub for tracking all types of work in the repository.

We distinguish between the following types of issues;

Issue Types        |   Description
-------------------| ------------------------------------------------------
`Bug Report`         | This `Issue` is dedicated to reporting a problem.
 `Task` | This `Issue` is used for describing and proposing a new work item (e.g., a new feature)

 If there are issues that link to the same topic, the creator of the issue shall mention those other tasks in the 
 description. To group tasks that can belong together, one could further create an issue mentioning and describing
 the overall user story for the referenced tasks.
 
## Pull Requests
Proposals for changes to the content of the repository are managed through Pull Requests (`PRs`). 

### Opening Pull Requests
To open such a `PR`, implement the changes in a new `feature branch`. Each `PR` must reference an issue and follows the 
naming schema: `<issue-number>-<feature-name>`. For a new `PR` the target branch is the `main` branch while the source 
branch is your `feature branch` The `feature branch` branch should be developed in a fork of the upstream repository. 
So before working on your first feature, you need to create such a fork (e.g., by pressing the `Fork` button in the top 
right corner of the GitHub page)

When opening a `PR` please consider the following topics:

* optional: Rebase your development on the branch to which you plan to create the `PR`.
* Each `PR` must be linked to an `Issue`:
    - Reference the `Issue` number in the name of your `feature branch` and the description of the `PR`.
    - Mention the `Issue` in one of the commit messages associated to the `PR` together with a GitHub keyword like 
    `closes #IssueNumber` or `fixes #IssuesNumber`. For more details visit the
     [GitHub documentation on linking PR with Issues](https://docs.github.com/en/github/managing-your-work-on-github/linking-a-pull-request-to-an-issue#linking-a-pull-request-to-an-issue-using-a-keyword)
* Each `PR` should only contain changes related to a single work item. If the changes cover more than one work item or 
  feature, then create one `PR` per work item. You may need to create new more specific `Issues` to reference if you 
  split up the work into multiple `feature branches`.
* Commit changes often. A `PR` may contain one or more commits.

## Eclipse Development Process

This Eclipse Foundation open project is governed by the Eclipse Foundation Development Process and
operates under the terms of the Eclipse IP Policy.

* https://eclipse.org/projects/dev_process
* https://www.eclipse.org/org/documents/Eclipse_IP_Policy.pdf

## Eclipse Contributor Agreement

In order to be able to contribute to Eclipse Foundation projects you must electronically sign the
Eclipse Contributor Agreement (ECA).

* http://www.eclipse.org/legal/ECA.php

The ECA provides the Eclipse Foundation with a permanent record that you agree that each of your
contributions will comply with the commitments documented in the Developer Certificate of Origin
(DCO). Having an ECA on file associated with the email address matching the "Author" field of your
contribution's Git commits fulfills the DCO's requirement that you sign-off on your contributions.

For more information, please see the Eclipse Committer Handbook:
https://www.eclipse.org/projects/handbook/#resources-commit

## Commit Messages 
Separate the subject from the body with a blank line because the subject line is shown in the Git
history and should summarize the commit body. Use the body to explain what and why with less focus
on the details of the how. This [blog post](https://chris.beams.io/posts/git-commit/#seven-rules)
has more tips and details. Before you push your commits to a repository, you should squash your
commits into one or more logical units of work, e.g., you should organize a new feature in a single
commit.

## License Headers & Licensing
All files contributed require headers - this will ensure the license and copyright clearing at the
end. Also, all contributions must have the same license as the source. The header should follow the
following template:

```
/*
 * Copyright (c) {YEAR} {NAME OF COMPANY X} 
 * Copyright (c) {YEAR} {NAME OF COMPANY Y} 
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
```

When using the template, one must replace "{NAME OF COMPANY X}" with the name of the involved
companies and "{YEAR}" with the year of the contribution. For each involved company you need a new
line starting with "Copyright" as outlined in the example.

The example is taken from a Java source file. If your file is of another type you may have to adapt
the comment syntax accordingly.

If you use third-party content (e.g., import / include ...), you are required to list each
third-party content explicitly with its version number in the documentation and your pull-request
comment. Please also check used third party material for license compatibility with the MPL-2.0.
E.g. software licensed under GPL, AGPL or, a similar strong copy-left license cannot be approved.

# Code Conventions
The ESMF SDK is written in the Java Programming Language. Please have a look into our [Code
Conventions](CONVENTIONS.md).

## Versioning
We use Semantic Versioning to identify released versions of the ESMF SDK. Semantic Versioning is
documented [here](https://semver.org). It proposes to have a versioning number with the following
elements:

````
Given a version number MAJOR.MINOR.PATCH, increment the:
- MAJOR version when you make incompatible API changes,
- MINOR version when you add functionality in a backwards compatible manner, and
- PATCH version when you make backwards compatible bug fixes.
Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.
````
 
Whereas the Major version must be incremented if the API has backward-incompatible changes (e.g., has breaking changes), 
the Minor version must be changed if new backward-compatible features are introduced and, 
the Patch version must be incremented if backward-compatible bugfixes are introduced.

### Breaking Changes
For the definition of a breaking change, we follow the definition as in the [Microsoft REST API
Guidelines](https://github.com/Microsoft/api-guidelines/blob/vNext/Guidelines.md#123-definition-of-a-breaking-change)
which are licensed under [CC-BY-4.0](https://creativecommons.org/licenses/by/4.0). This definition
states:
````
Changes to the contract of an API are considered a breaking change. Changes that impact the backwards compatibility 
of an API are a breaking change.
`````

### Version Syntax for Specific Environments

Git version tag

vX.Y.Z-[pre-release-identifier]

Examples:

v1.0.0-RC1, v1.0.0

# Resources

* [For a Repo](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request-from-a-fork)
* [Issue Creation](https://help.github.com/en/github/managing-your-work-on-github/creating-an-issue)
* [PR Creation](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request)


