# Gitlet

### Overview 
Gitlet is a distributed version control system that mimics some of the basic features of Git

### Main Features
The main functionality that Gitlet supports is:

- Saving the contents of entire directories of files. In Gitlet, this is called committing, and the saved contents themselves are called commits.
- Restoring a version of one or more files or entire commits. In Gitlet, this is called checking out those files or that commit.
- Viewing the history of your backups. In Gitlet, you view this history in something called the log.
- Maintaining related sequences of commits, called branches
- Merging changes made in one branch into another.


### Project Structure

![gitlet](https://github.com/user-attachments/assets/96ef11d9-7a53-4504-88e0-3845b86066cb)

- **Staging area**: 
  - contain the working directory tracked files.
  - files stages in addition will be added tracked in the next commit.
  - files staged in removal will be removed in the next commit or unstaged if they are currently staged for addition 
- **Commit**: 
  - is an object save a snapshot of tracked files in the current commit and staging area
  - each commit’s snapshot of files will be exactly the same as its parent commit’s snapshot of files; it will keep versions of files exactly as they are, and not update them it only updates the contents of files it is tracking that have been staged for addition at the time of commit
  - if the file is staged for removal it will be untracked in the new commit
  - each commit contains data like:
    - commit message
    - commit timestamp
    - references to blobs (tracked file)
    - references to parent commits
  - commit trees are immutable: once a commit node has been created, it can never be destroyed (or changed at all).
  - We can only add new things to the commit tree, not modify existing things.
- **Blobs**:
  - The saved contents of files, and gitlet saves many versions of files
  - a single file might correspond to multiple blobs and each blob tracked in a different commit
- **Branches**:
  - Branch allows us to create different versions and switch between them
  - It points to the commit in the new version
- **HEAD pointer**:
  - The head pointer keeps track of where the current branch and current commit
  - the current branch is the version that reflects the current state of the files

**Every object (blob, commit) has  a unique integer ID that serves as a reference to the object two objects with exactly the same content will have the same this ID is generated by using a cryptographic hash function called SHA-1**


![gitelet-commit drawio](https://github.com/user-attachments/assets/f0fa13b0-9bd3-4754-9e2a-928a0ef47d86)

![gitelet-commit(branches)](https://github.com/user-attachments/assets/1ed99d2f-6ac4-4b76-ab86-b63617867a3b)

### Usage

**Clone the Repository**
``` shell
git clone https://github.com/AhmedMabrouk22/Gitlet.git
cd Gitlet/proj2
```
**Compile the Code** 
```shell
javac ./gitlet/*.java
```

**Run Commands**
Use the `java gitlet.Main` command followed by Gitlet commands

```shell
java gitlet.Main init
```


### Commands and Functions
it's a simplified implementation of Git supported commands such as:

- `init`
  - Creates a new Gitlet version-control system in the current directory.
  - automatically start with the initial commit
  - it will have a single branch `master` and it points to the initial commit
- `add [file name]`
  - Adds a copy of the file as it currently exists to the staging area.
  - f the current working version of the file is identical to the version in the current commit, do not stage it to be added, and remove it from the staging area if it is already there
- `commit`
  - Saves a snapshot of tracked files in the current commit and staging area, creating a new commit.
  - Any changes made to files after staging for addition or removal are ignored by the `commit`
- `rm [file name]`
  - Unstage the file if it is currently staged for addition
  - If the file is tracked in the current commit, stage it for removal and remove the file from the working directory if the user has not already done so (do not remove it unless it is tracked in the current commit).
- `log`
  - Starting at the current head commit, display information about each commit backwards along the commit tree until the initial commit
- `global-log`  
  - Like `log`, displays information about all commits ever made.
- `find [commit message]`
  - Prints out the IDs of all commits that have the given commit message.
- `status`
  - Displays what branches currently exist and displays what files have been staged for addition or removal.
  - marks the current branch with a `*`
- `checkout -- [file name]`
  - Takes the version of the file as it exists in the head commit and puts it in the working directory.
- `checkout [commit id] -- [file name]`
  - Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory.
- `checkout [branch name]`
  - Takes all files in the commit at the head of the given branch, and puts them in the working directory.
- `branch [branch name]`
  - Creates a new branch with the given name, and points it at the current head commit.
  - Before you
    ever call the branch, the default branch is still `master`.
- `rm-branch [branch name]`: 
  - Deletes the branch with the given name.
- `reset [commit id]`: 
  - Checks out all the files tracked by the given commit.
- `merge [branch name]`: 
  - Merges files from the given branch into the current branch.

### Coming Features and Commands

1. [ ] Enhanced status command and add modifications not staged for commit, untracked files
2. [ ] Add Basic Remote Repository Commands:
   - `add-remote`
   - `rm-remote`
   - `push`
   - `fetch`
   - `pull`
