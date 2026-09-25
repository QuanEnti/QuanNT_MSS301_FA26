# Git Commit Conventions — MSS301 FA26

> Theo chuẩn **Conventional Commits 1.0.0**  
> Tham khảo: https://www.conventionalcommits.org/

---

## 1. Format tổng quát

```
<type>(<scope>): <subject>

<body>

<footer>
```

| Phần | Bắt buộc | Mô tả |
|------|----------|-------|
| `type` | ✅ | Loại commit (feat, fix, docs…) |
| `scope` | ☑️ | Phần code bị ảnh hưởng (entity, service, ci…) |
| `subject` | ✅ | Tóm tắt ≤ 50 ký tự, imperative mood |
| `body` | ☑️ | Giải thích what và why, wrap 72 char |
| `footer` | ☑️ | Reference issue, breaking change |

---

## 2. Danh sách Type

| Type | Khi nào dùng | Ví dụ |
|------|-------------|-------|
| `feat` | Thêm tính năng mới | `feat: add CRUD for Book entity` |
| `fix` | Sửa bug | `fix: prevent NPE in StudentDAO.update()` |
| `docs` | Chỉ sửa documentation | `docs: update README with H2 setup` |
| `style` | Format code, không đổi logic | `style: reformat with google-java-format` |
| `refactor` | Đổi cấu trúc, không thêm feature/fix bug | `refactor: extract validateStudent helper` |
| `perf` | Cải thiện performance | `perf: add @EntityGraph to avoid N+1` |
| `test` | Thêm/sửa test | `test: add unit tests for StudentServiceImpl` |
| `chore` | Không ảnh hưởng src/test | `chore: bump Spring Boot to 3.5.14` |
| `build` | Build system, Maven, dependency | `build: add h2database test dependency` |
| `ci` | CI config (GitHub Actions…) | `ci: add Java 21 matrix to workflow` |
| `revert` | Revert commit trước | `revert: feat(service): add caching` |

> **Tip:** Khi không chắc, dùng `chore`. Nhưng cố gắng dùng đúng type.

---

## 3. Quy tắc Subject

### ✅ DO
| Quy tắc | Ví dụ tốt |
|---------|----------|
| Imperative mood | `add login validation` |
| Viết thường | `fix typo in error message` |
| Không dấu chấm cuối | `update README` |
| ≤ 50 ký tự | `fix StudentDAO update method` |

### ❌ DON'T
| Sai | Đúng |
|-----|------|
| `Added new feature` (past tense) | `add new feature` |
| `Fixes bug in service` (3rd person) | `fix bug in service` |
| `update.` (có chấm) | `update` |
| `stuff / wip / fix` (vô nghĩa) | `fix: handle null email in StudentService.create()` |

---

## 4. Templates

### 4.1 feat — Thêm tính năng
```
feat(<scope>): <add what feature>

<Why this feature is needed. What problem it solves.>

Closes #<issue-number>
```

### 4.2 fix — Sửa bug
```
fix(<scope>): <what bug is fixed>

<Mô tả bug. Root cause. Solution.>

Fixes #<issue-number>
```

### 4.3 docs — Documentation
```
docs(<scope>): <what doc is changed>

<Why this update is needed.>
```

### 4.4 refactor — Đổi cấu trúc
```
refactor(<scope>): <what is refactored>

<Why refactor is needed. What pattern is applied.>
Behavior không đổi với production code.
```

### 4.5 test — Thêm/sửa test
```
test(<scope>): <what is tested>
```

### 4.6 BREAKING CHANGE
```
feat(<scope>)!: <subject>

BREAKING CHANGE: <description of what changed and migration guide>
```

---

## 5. Ví dụ cho HSF302

```bash
# Entity
feat(entity): implement Student entity with JPA annotations
feat(entity): implement Book entity with @ManyToOne relationship

# DAO
feat(dao): implement save() method in StudentDAO
feat(dao): implement findById() and findAll() in StudentDAO
feat(dao): implement update() and delete() with transaction handling

# Repository
feat(repository): implement StudentRepositoryImpl as DAO adapter

# Service
feat(service): implement create/getById/getAll/update/deleteById
feat(service): add input validation for Student fields

# Fix
fix(dao): correct exception handling in StudentDAO.save()
fix(service): use exact error message "First name must not be blank"
fix(entity): add missing @JoinColumn for Book.student relation

# Refactor
refactor(service): extract validateStudent helper method
refactor(dao): use try-with-resources for EntityManager

# Test
test(entity): add 9 unit tests for Student JPA annotations
test(dao): add 10 unit tests for StudentDAO CRUD with H2
test(service): add 17 unit tests for StudentServiceImpl validation

# Docs
docs(slides): add Chapter 01 JPA Mapping notes
docs(readme): add H2 in-memory setup instructions

# Chore / Build / CI
chore: remove unused imports across pojo package
build(pom): add h2database test scope dependency
ci(classroom): add autograding workflow for GitHub Classroom
```

---

## 6. Quy ước Branch

```
<type>/<short-description>
<type>/<issue-number>-<short-description>
```

### Ví dụ
```bash
# Sinh viên làm bài
git checkout -b feat/todo-1-student-entity
git checkout -b feat/todo-3-student-dao-crud
git checkout -b fix/todo-5-validation-message

# Maintenance
git checkout -b docs/update-readme-h2-setup
git checkout -b chore/bump-spring-boot-3-5-14
```

### Quy tắc
- Dùng dấu gạch ngang `-`, không gạch dưới `_` hay space
- Viết thường
- Ngắn gọn (≤ 50 ký tự sau prefix)
- Tránh tên người, ngày tháng: ~~`feat/quan-2026-09-25-search`~~

---

## 7. Cheat Sheet

```
┌─────────────────────────────────────────────────────────────────┐
│              CONVENTIONAL COMMITS CHEAT SHEET                   │
├─────────────────────────────────────────────────────────────────┤
│  FORMAT:   <type>(<scope>): <subject>                           │
│                                                                 │
│  TYPES:                                                         │
│    feat      → New feature                                      │
│    fix       → Bug fix                                          │
│    docs      → Documentation only                               │
│    style     → Formatting, no logic change                      │
│    refactor  → Restructure code, no behavior change             │
│    perf      → Performance improvement                          │
│    test      → Add/modify tests                                 │
│    chore     → Maintenance, deps update                         │
│    build     → Build system, Maven, npm                         │
│    ci        → CI/CD config (GitHub Actions...)                 │
│    revert    → Revert previous commit                           │
│                                                                 │
│  RULES:                                                         │
│    ✓ Imperative mood: "add" not "added" / "adds"                │
│    ✓ Lower case subject                                         │
│    ✓ No period at end                                           │
│    ✓ Max 50 chars in subject                                    │
│    ✓ Body wraps at 72 chars                                     │
│                                                                 │
│  BRANCH:   <type>/<short-desc>                                  │
│    feat/student-search-by-email                                 │
│    fix/transaction-leak-in-dao                                  │
│    docs/update-readme-h2-setup                                  │
└─────────────────────────────────────────────────────────────────┘
```
