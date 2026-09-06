# README Update and Main Branch Sync Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Publish the current `newlife` project version as the repository default version and replace the stale README with an accurate, recruiter-friendly project overview.

**Architecture:** Make documentation-only changes on `newlife`, verify every documented flow against the current source tree, create an isolated documentation commit, then fast-forward both remote branches to the same verified commit. Preserve all unrelated working-tree changes.

**Tech Stack:** Git, Markdown, Spring Boot 2.7, Vue 2, MySQL 8

---

### Task 1: Rewrite the project README

**Files:**
- Modify: `README.md`
- Create: `docs/images/dashboard.png`
- Create: `docs/images/audit.png`
- Create: `docs/images/result.png`

- [ ] **Step 1: Replace the stale overview with an honest project position**

State that this is a course team project for learning and demonstration, not a claim of government production deployment.

- [ ] **Step 2: Describe the current business flow**

Document the current state progression exactly as:

```text
0 草稿 -> 1 待区县审核 -> 2 区县审核通过 -> 5 已上报市级 -> 3 市级通过
                                                        \-> 4 驳回
```

Also explain that rejected records can be modified and resubmitted.

- [ ] **Step 3: Keep only useful project-level information**

Include the technology stack, three user roles, five principal functional areas, architecture, concise directory layout, setup steps, test-account warning, limitations, and contribution guidance. Remove the exhaustive table-by-table and FAQ material that obscures the project overview.

- [ ] **Step 4: Add three unchanged presentation screenshots**

Export pages 14, 17, and 18 from the supplied course-presentation PDF as `dashboard.png`, `audit.png`, and `result.png`, then inspect all three images for readability and sensitive information.

- [ ] **Step 5: Validate documented repository paths**

Run:

```powershell
Test-Path README.md
Test-Path park-server/pom.xml
Test-Path park-admin/package.json
Test-Path park-server/sql/complete_schema.sql
Test-Path park-server/src/main/resources/application-dev.yml.example
```

Expected: all values are `True`.

### Task 2: Verify and commit documentation

**Files:**
- Modify: `README.md`
- Create: `docs/images/dashboard.png`
- Create: `docs/images/audit.png`
- Create: `docs/images/result.png`
- Create: `docs/superpowers/plans/2026-09-06-readme-and-main-sync.md`
- Modify: `docs/superpowers/specs/2026-09-06-readme-and-main-sync-design.md` only to remove trailing whitespace if needed

- [ ] **Step 1: Check content and whitespace**

Run:

```powershell
rg -n "status|已上报|课程团队项目|非生产" README.md
git diff --check
```

Expected: the current status `5` flow and project disclaimer are present; `git diff --check` returns no errors.

- [ ] **Step 2: Inspect the exact staged file list**

Run:

```powershell
git add -- README.md docs/images/dashboard.png docs/images/audit.png docs/images/result.png docs/superpowers/plans/2026-09-06-readme-and-main-sync.md docs/superpowers/specs/2026-09-06-readme-and-main-sync-design.md
git diff --cached --name-only
```

Expected: only the named README, screenshot, plan, and design files appear; `JwtAuthenticationFilter.java` and local answer materials do not appear.

- [ ] **Step 3: Commit the README update**

Run:

```powershell
git commit -m "docs: 更新项目说明与审核流程"
```

Expected: one documentation commit on `newlife`.

### Task 3: Publish the current version

**Files:** None

- [ ] **Step 1: Reconfirm fast-forward safety**

Run:

```powershell
git fetch origin main newlife
git merge-base --is-ancestor origin/main HEAD
git merge-base --is-ancestor origin/newlife HEAD
```

Expected: both commands exit with status `0`.

- [ ] **Step 2: Push the same commit to both branches without force**

Run:

```powershell
git push origin HEAD:newlife HEAD:main
```

Expected: both remote branches update to the same commit; no force push is used.

- [ ] **Step 3: Verify remote state and public README**

Run:

```powershell
git ls-remote --heads origin main newlife
```

Expected: `refs/heads/main` and `refs/heads/newlife` have the same commit SHA. Then fetch the public raw README and confirm its title, disclaimer, and status-5 flow.
