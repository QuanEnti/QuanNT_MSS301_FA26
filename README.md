# QuanNT — MSS301 FA26

> **Học phần:** MSS301 — Middle-tier Software Solutions  
> **Kỳ:** FA2026 | **Sinh viên:** QuanNT  
> **Repo:** [QuanEnti/QuanNT_MSS301_FA26](https://github.com/QuanEnti/QuanNT_MSS301_FA26)

---

## 📁 Cấu trúc thư mục

```
QuanNT_MSS301_FA26/
├── chapter1-exercise1/        # JPA Mapping cơ bản
├── chapter2-exercise1/        # Spring Framework intro
├── chapter3-exercise1/        # Spring Boot starter
├── SlideNotes/                # Ghi chú bài giảng theo chapter
│   ├── Chapter01-JPA-Mapping.md
│   └── ...
└── README.md
```

---

## 🚀 Cài đặt & Chạy

### Yêu cầu
- Java 21+
- Maven 3.9+
- IntelliJ IDEA (khuyến nghị)

### Clone & mở project
```bash
git clone https://github.com/QuanEnti/QuanNT_MSS301_FA26.git
cd QuanNT_MSS301_FA26
```

### Chạy test (H2 in-memory, không cần MSSQL)
```bash
cd chapter1-exercise1
mvn clean test
```

---

## 📝 Quy ước commit

Project dùng **Conventional Commits 1.0.0**.

```
<type>(<scope>): <subject>
```

| Type | Khi dùng |
|------|----------|
| `feat` | Thêm tính năng mới |
| `fix` | Sửa bug |
| `docs` | Chỉ sửa documentation |
| `refactor` | Đổi cấu trúc, không đổi behavior |
| `test` | Thêm/sửa test |
| `chore` | Maintenance, deps |
| `build` | Build system, Maven |
| `ci` | CI/CD config |

**Ví dụ:**
```
feat(entity): implement Student entity with JPA annotations
fix(dao): correct exception handling in StudentDAO.save()
test(service): add 17 unit tests for StudentServiceImpl
docs(readme): add H2 setup instructions
```

📄 Xem đầy đủ: [COMMIT_CONVENTION.md](./COMMIT_CONVENTION.md)

---

## 🌿 Quy ước Branch

```
<type>/<short-description>
```

```bash
git checkout -b feat/todo-1-student-entity
git checkout -b fix/todo-5-validation-message
git checkout -b docs/update-readme-h2-setup
```

---

## 📊 Progress

| Chapter | Exercise | Status |
|---------|----------|--------|
| Chapter 1 | JPA Mapping | 🔄 In Progress |
| Chapter 2 | Spring Framework | ⏳ Pending |
| Chapter 3 | Spring Boot | ⏳ Pending |

---

## 📚 SlideNotes

Ghi chú bài giảng theo từng chapter nằm trong thư mục [`SlideNotes/`](./SlideNotes/).
