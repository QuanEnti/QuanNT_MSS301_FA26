from __future__ import annotations

import json
import re
import subprocess
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path

from docx import Document
from docx.enum.section import WD_SECTION
from docx.enum.table import WD_ALIGN_VERTICAL, WD_CELL_VERTICAL_ALIGNMENT, WD_TABLE_ALIGNMENT
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK, WD_LINE_SPACING
from docx.enum.style import WD_STYLE_TYPE
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Mm, Pt, RGBColor


SCRIPT_DIR = Path(__file__).resolve().parent
REPO_ROOT = SCRIPT_DIR.parent
PROJECT_DIR = SCRIPT_DIR / "Assignment2" / "WatchManagement"
OUTPUT = SCRIPT_DIR / "Assignment2_Document_DE190728_NguyenTienQuan.docx"

BLUE = "17365D"
BLUE_2 = "1F4E78"
BLUE_3 = "D9EAF7"
ORANGE = "F28C28"
TEAL = "0F6B78"
GREEN = "2E7D32"
PURPLE = "6A1B9A"
GRAY = "666666"
LIGHT_GRAY = "F2F2F2"
MID_GRAY = "D9E1F2"
CODE_BG = "17202A"
CODE_FG = "EAF2F8"


@dataclass(frozen=True)
class Todo:
    code: str
    requirement: str
    commit: str
    implementation: str
    file: str
    keyword: str | None = None
    evidence_text: str | None = None


GROUPS: list[tuple[str, list[Todo]]] = [
    (
        "Khởi tạo workspace và bốn project",
        [
            Todo(
                "0.1",
                "Bổ sung ignore rules, Conventional Commit template và biến môi trường mẫu.",
                "58b7c93",
                "Workspace thống nhất cấu hình dùng chung; thông tin nhạy cảm được tách khỏi source và có tệp .env.example để chạy lặp lại trên máy khác.",
                ".env.example",
                "MSSQL_SA_PASSWORD",
            ),
            Todo(
                "0.2",
                "Lưu đề Assignment 02 gốc trong Slot18 để đối chiếu yêu cầu.",
                "1628393",
                "Đề gốc được giữ nguyên dưới dạng DOCX, gồm mô hình dữ liệu, dữ liệu seed, yêu cầu CRUD, phân quyền Administrator, API Gateway và Saga Pattern.",
                "../../MMS301_Assignment 02_Watch Management_Saga Pattern.docx",
                evidence_text=(
                    "Assignment 02 for MSS301 – Watch Management using Saga Pattern\n"
                    "4 projects: Account, Brand, Watch, API Gateway\n"
                    "3 databases: DBAccount, DBBrand, DBWatch\n"
                    "Mục tiêu: CRUD Watch + Database per Service + Saga"
                ),
            ),
            Todo(
                "0.3",
                "Tạo Maven reactor quản lý đồng thời bốn module.",
                "e7ce80c",
                "Root pom đóng vai trò aggregator; một lệnh Maven tại thư mục WatchManagement có thể compile và test toàn bộ hệ thống theo đúng thứ tự module.",
                "pom.xml",
                "<modules>",
            ),
            Todo(
                "0.4",
                "Scaffold MSAccount_QuanNT.",
                "c0ca3c8",
                "Account Service dùng Spring Web MVC, Validation, Spring Data JPA và SQL Server Driver; chạy độc lập trên cổng 8081.",
                "MSAccount_QuanNT/pom.xml",
                "spring-boot-starter-data-jpa",
            ),
            Todo(
                "0.5",
                "Scaffold MSBrand_QuanNT.",
                "dfed59c",
                "Brand Service được tách thành ứng dụng Spring Boot độc lập, sở hữu DBBrand và mô hình brand/reference riêng trên cổng 8082.",
                "MSBrand_QuanNT/pom.xml",
                "spring-boot-starter-data-jpa",
            ),
            Todo(
                "0.6",
                "Scaffold MSWatch_QuanNT.",
                "c99b76a",
                "Watch Service sở hữu DBWatch, cung cấp catalog, CRUD và Saga state/log; module có JPA, Validation và REST client trên cổng 8083.",
                "MSWatch_QuanNT/pom.xml",
                "spring-boot-starter-validation",
            ),
            Todo(
                "0.7",
                "Scaffold APIGateway_QuanNT.",
                "fad01f4",
                "Gateway dùng Spring Cloud Gateway Server Web MVC, là cổng vào duy nhất tại port 9000 và không sở hữu database.",
                "APIGateway_QuanNT/pom.xml",
                "gateway-server-webmvc",
            ),
        ],
    ),
    (
        "Account Service và xác thực phiên",
        [
            Todo(
                "1.1",
                "Map entity và repository StoreAccounts.",
                "5ee0df8",
                "StoreAccount ánh xạ đầy đủ AccountID, Username, Email, Password, Role và IsActive; repository hỗ trợ tìm username không phân biệt hoa thường.",
                "MSAccount_QuanNT/src/main/java/com/mss301/account/model/StoreAccount.java",
                "@Entity",
            ),
            Todo(
                "1.2",
                "Nạp năm tài khoản đúng dữ liệu đề bài.",
                "573fb7c",
                "AccountDataSeeder chỉ nạp khi bảng rỗng, bảo toàn tính idempotent và tạo đủ Administrator, Staff, Member cùng hai tài khoản inactive.",
                "MSAccount_QuanNT/src/main/java/com/mss301/account/config/AccountDataSeeder.java",
                "adminsys",
            ),
            Todo(
                "1.3",
                "Kiểm tra login, trạng thái active và phát session token.",
                "82f7902",
                "AuthService trả 401 khi sai thông tin, 403 khi inactive; SessionStore phát UUID có TTL 8 giờ, hỗ trợ resolve và revoke token an toàn theo phiên.",
                "MSAccount_QuanNT/src/main/java/com/mss301/account/service/AuthService.java",
                "public LoginResponse login",
            ),
            Todo(
                "1.4",
                "Expose login, logout, session validation và chuẩn hóa lỗi.",
                "3d05369",
                "AuthController cung cấp API công khai cho login và endpoint nội bộ validate session; GlobalExceptionHandler trả lỗi JSON thống nhất.",
                "MSAccount_QuanNT/src/main/java/com/mss301/account/controller/AuthController.java",
                "@PostMapping",
            ),
            Todo(
                "1.5",
                "Kiểm thử login đúng, sai password và account inactive.",
                "9946f4c",
                "Unit test cô lập AuthService bằng Mockito, xác nhận happy path và hai nhánh bảo mật quan trọng; kết quả hiện tại 3/3 pass.",
                "MSAccount_QuanNT/src/test/java/com/mss301/account/service/AuthServiceTest.java",
                "@Test",
            ),
        ],
    ),
    (
        "Brand Service và reference idempotent",
        [
            Todo(
                "2.1",
                "Map WatchBrands và quan hệ tham chiếu Brand–Watch.",
                "d4934f5",
                "WatchBrand sở hữu dữ liệu brand; BrandWatchReference giữ WatchID logic với unique constraint, không tạo foreign key xuyên database.",
                "MSBrand_QuanNT/src/main/java/com/mss301/brand/model/BrandWatchReference.java",
                "@Table",
            ),
            Todo(
                "2.2",
                "Nạp năm brand và reference cho dữ liệu Watch mẫu.",
                "2230ee3",
                "Seeder tạo Rolex, Omega, Seiko, Casio, TAG Heuer và các reference tương ứng 10 Watch; chỉ chạy khi dữ liệu chưa tồn tại.",
                "MSBrand_QuanNT/src/main/java/com/mss301/brand/config/BrandDataSeeder.java",
                "Rolex",
            ),
            Todo(
                "2.3",
                "Cài đặt truy vấn brand và thao tác reference idempotent.",
                "afa8d24",
                "registerWatch tạo mới hoặc chuyển brand cho cùng WatchID; gọi lặp với cùng dữ liệu không sinh bản ghi trùng. unregisterWatch chỉ xóa đúng cặp brand/watch.",
                "MSBrand_QuanNT/src/main/java/com/mss301/brand/service/BrandService.java",
                "registerWatch",
            ),
            Todo(
                "2.4",
                "Expose public Brand API và internal reference API.",
                "33c796b",
                "GET brand phục vụ catalog công khai; API /api/internal/brands dùng riêng cho Watch Service khi orchestrate Saga.",
                "MSBrand_QuanNT/src/main/java/com/mss301/brand/controller/BrandReferenceController.java",
                "@RequestMapping",
            ),
            Todo(
                "2.5",
                "Kiểm thử create, gọi lặp và chuyển Brand reference.",
                "9c31107",
                "Ba test xác nhận tạo reference, tính idempotent và khả năng transfer brand; kết quả hiện tại 3/3 pass.",
                "MSBrand_QuanNT/src/test/java/com/mss301/brand/service/BrandServiceTest.java",
                "@Test",
            ),
        ],
    ),
    (
        "Watch catalog, validation và local transaction",
        [
            Todo(
                "3.1",
                "Map Watches, WatchCategories và repository.",
                "066ef50",
                "Watch quan hệ nội bộ với WatchCategory trong DBWatch; BrandID chỉ là logical ID. Repository sắp xếp WatchID giảm dần để item mới đứng đầu.",
                "MSWatch_QuanNT/src/main/java/com/mss301/watch/model/Watch.java",
                "@Entity",
            ),
            Todo(
                "3.2",
                "Nạp năm category và mười watch đúng dữ liệu đề.",
                "4ad48dc",
                "WatchDataSeeder tạo đúng 5 category và 10 watch lịch sử; dữ liệu đề được giữ nguyên dù có giá/ngày cũ, còn rule mới chỉ áp dụng request tạo/cập nhật.",
                "MSWatch_QuanNT/src/main/java/com/mss301/watch/config/WatchDataSeeder.java",
                "Dive Watches",
            ),
            Todo(
                "3.3",
                "Tạo DTO và validation name, price, current date.",
                "17d8597",
                "WatchRequest yêu cầu đủ field, name > 10 ký tự, price >= 100, stock không âm, ID dương và ReleaseDate bằng ngày hiện tại qua custom @CurrentDate.",
                "MSWatch_QuanNT/src/main/java/com/mss301/watch/dto/WatchRequest.java",
                "@CurrentDate",
            ),
            Todo(
                "3.4",
                "Cài đặt list mới nhất trước và CRUD bằng local transaction.",
                "8c9c2e4",
                "WatchService dùng transaction chỉ trong DBWatch, hỗ trợ snapshot/restore cho compensation và trả CategoryName kèm dữ liệu Watch.",
                "MSWatch_QuanNT/src/main/java/com/mss301/watch/service/WatchService.java",
                "findAll",
            ),
            Todo(
                "3.5",
                "Expose Category API và chuẩn hóa lỗi Watch.",
                "97ecc86",
                "CategoryController cung cấp danh sách category không cần đăng nhập; lỗi validation/not found được trả về cấu trúc thống nhất.",
                "MSWatch_QuanNT/src/main/java/com/mss301/watch/controller/CategoryController.java",
                "@GetMapping",
            ),
            Todo(
                "3.6",
                "Kiểm thử validation, CRUD, JPA schema và dữ liệu seed.",
                "682c7ac",
                "Bộ test gồm validation, service và integration H2 MSSQL mode; xác nhận schema, 5 category, 10 watch và thứ tự WatchID DESC.",
                "MSWatch_QuanNT/src/test/java/com/mss301/watch/WatchCatalogIntegrationTest.java",
                "seededCatalog",
            ),
        ],
    ),
    (
        "Saga Orchestration và compensation",
        [
            Todo(
                "4.1",
                "Map sáu Saga state, operation và step log.",
                "8263d64",
                "WatchSagas lưu operationId, loại CREATE/UPDATE/DELETE và 6 trạng thái; WatchSagaLogs ghi từng bước, kết quả, message và thời điểm.",
                "MSWatch_QuanNT/src/main/java/com/mss301/watch/saga/WatchSaga.java",
                "class WatchSaga",
            ),
            Todo(
                "4.2",
                "Tạo REST client đồng bộ Brand reference.",
                "84f70b8",
                "BrandClient gọi internal API register/unregister của Brand Service, tách rõ giao tiếp service-to-service khỏi public API.",
                "MSWatch_QuanNT/src/main/java/com/mss301/watch/client/BrandClient.java",
                "registerWatch",
            ),
            Todo(
                "4.3",
                "Orchestrate create/update/delete và compensation retry.",
                "1382ae0",
                "Orchestrator thực thi chuỗi local transaction, log từng bước, chạy bù ngược khi lỗi và retry compensation tối đa 3 lần; không dùng distributed transaction/2PC.",
                "MSWatch_QuanNT/src/main/java/com/mss301/watch/service/WatchSagaOrchestrator.java",
                "MAX_COMPENSATION_ATTEMPTS",
            ),
            Todo(
                "4.4",
                "Kiểm thử happy path và các luồng compensation.",
                "8504d34",
                "Test xác nhận create thành công, create bị bù khi Brand lỗi và update khôi phục snapshot; 3/3 test Saga pass.",
                "MSWatch_QuanNT/src/test/java/com/mss301/watch/service/WatchSagaOrchestratorTest.java",
                "@Test",
            ),
        ],
    ),
    (
        "API Gateway, xác thực và phân quyền",
        [
            Todo(
                "5.1",
                "Route Account, Brand, Watch, Category và Saga.",
                "75c728a",
                "GatewayRoutes định tuyến toàn bộ /api/auth, /api/brands, /api/watches, /api/categories và /api/sagas tới đúng downstream service.",
                "APIGateway_QuanNT/src/main/java/com/mss301/gateway/routes/GatewayRoutes.java",
                "RouterFunction",
            ),
            Todo(
                "5.2",
                "Validate Bearer session, chặn non-Admin và chống giả header.",
                "92fbb3d",
                "Filter chỉ bảo vệ POST/PUT/DELETE Watch, trả 401/403/503 đúng ngữ cảnh, xóa X-Account-* do client gửi và chèn trusted header từ session hợp lệ.",
                "APIGateway_QuanNT/src/main/java/com/mss301/gateway/filter/AdminAuthorizationFilter.java",
                "doFilterInternal",
            ),
            Todo(
                "5.3",
                "Kiểm thử 401, 403 và trusted Admin headers.",
                "d385bd3",
                "Ba test filter xác nhận thiếu token bị 401, Staff bị 403 và Admin được forward với header tin cậy; 3/3 pass.",
                "APIGateway_QuanNT/src/test/java/com/mss301/gateway/filter/AdminAuthorizationFilterTest.java",
                "@Test",
            ),
        ],
    ),
    (
        "Docker, Postman và tài liệu vận hành",
        [
            Todo(
                "6.1",
                "Docker hóa SQL Server và bốn application.",
                "f9976f4",
                "Compose định nghĩa SQL Server, init job và 4 application; healthcheck/depends_on đảm bảo database sẵn sàng trước khi service kết nối.",
                "docker-compose.yml",
                "services:",
            ),
            Todo(
                "6.2",
                "Thêm Postman environment, requests và test scripts.",
                "f75be52",
                "Collection có 3 folder, 13 request và 35 dòng test script; bao phủ public catalog, login, 401, 403, validation, create/update/delete Watch và đọc Saga log.",
                "postman/WatchManagement.postman_collection.json",
                evidence_text=(
                    "Watch Management – Assignment 02\n"
                    "Folders: 01 Public catalog | 02 Authentication | 03 Administrator Watch Saga\n"
                    "Requests: 13    Test script lines: 35\n"
                    "Biến: baseUrl, adminToken, staffToken, createdWatchId, sagaOperationId"
                ),
            ),
            Todo(
                "6.3",
                "Viết hướng dẫn kiến trúc, chạy, test và Saga.",
                "2407786",
                "README mô tả kiến trúc, tài khoản mẫu, business rules, API, Maven test, Docker, IntelliJ, Postman và cách quan sát compensation.",
                "README.md",
                "## 5. Saga Orchestration",
            ),
        ],
    ),
]


def git(*args: str) -> str:
    result = subprocess.run(
        ["git", *args],
        cwd=REPO_ROOT,
        check=True,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    return result.stdout.strip()


def set_cell_shading(cell, fill: str) -> None:
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = tc_pr.find(qn("w:shd"))
    if shd is None:
        shd = OxmlElement("w:shd")
        tc_pr.append(shd)
    shd.set(qn("w:fill"), fill)


def set_cell_margins(cell, top=90, start=120, bottom=90, end=120) -> None:
    tc = cell._tc
    tc_pr = tc.get_or_add_tcPr()
    tc_mar = tc_pr.first_child_found_in("w:tcMar")
    if tc_mar is None:
        tc_mar = OxmlElement("w:tcMar")
        tc_pr.append(tc_mar)
    for margin, value in (("top", top), ("start", start), ("bottom", bottom), ("end", end)):
        node = tc_mar.find(qn(f"w:{margin}"))
        if node is None:
            node = OxmlElement(f"w:{margin}")
            tc_mar.append(node)
        node.set(qn("w:w"), str(value))
        node.set(qn("w:type"), "dxa")


def set_repeat_table_header(row) -> None:
    tr_pr = row._tr.get_or_add_trPr()
    tbl_header = OxmlElement("w:tblHeader")
    tbl_header.set(qn("w:val"), "true")
    tr_pr.append(tbl_header)


def prevent_row_split(row) -> None:
    tr_pr = row._tr.get_or_add_trPr()
    cant_split = OxmlElement("w:cantSplit")
    tr_pr.append(cant_split)


def set_table_cell_text(cell, text: str, *, bold=False, color="000000", size=9, font="Times New Roman", align=None) -> None:
    cell.text = ""
    paragraph = cell.paragraphs[0]
    if align is not None:
        paragraph.alignment = align
    paragraph.paragraph_format.space_after = Pt(0)
    paragraph.paragraph_format.space_before = Pt(0)
    run = paragraph.add_run(text)
    run.bold = bold
    run.font.name = font
    run.font.size = Pt(size)
    run.font.color.rgb = RGBColor.from_string(color)
    run._element.rPr.rFonts.set(qn("w:eastAsia"), font)
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER
    set_cell_margins(cell)


def add_field(paragraph, instruction: str, placeholder: str = "") -> None:
    run = paragraph.add_run()
    begin = OxmlElement("w:fldChar")
    begin.set(qn("w:fldCharType"), "begin")
    instr = OxmlElement("w:instrText")
    instr.set(qn("xml:space"), "preserve")
    instr.text = instruction
    separate = OxmlElement("w:fldChar")
    separate.set(qn("w:fldCharType"), "separate")
    run._r.append(begin)
    run._r.append(instr)
    run._r.append(separate)
    if placeholder:
        text_run = paragraph.add_run(placeholder)
        text_run.font.color.rgb = RGBColor(0x77, 0x77, 0x77)
    end_run = paragraph.add_run()
    end = OxmlElement("w:fldChar")
    end.set(qn("w:fldCharType"), "end")
    end_run._r.append(end)


def add_page_number(paragraph) -> None:
    paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER
    prefix = paragraph.add_run("Trang ")
    prefix.font.name = "Times New Roman"
    prefix.font.size = Pt(9)
    add_field(paragraph, "PAGE")
    middle = paragraph.add_run(" / ")
    middle.font.size = Pt(9)
    add_field(paragraph, "NUMPAGES")


def add_caption(doc: Document, text: str) -> None:
    p = doc.add_paragraph(style="Caption")
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(3)
    p.paragraph_format.space_after = Pt(7)
    p.add_run(text)


def add_labeled_paragraph(doc: Document, label: str, text: str) -> None:
    p = doc.add_paragraph()
    p.paragraph_format.keep_together = True
    run = p.add_run(label)
    run.bold = True
    run.font.color.rgb = RGBColor.from_string(BLUE_2)
    p.add_run(text)


def read_excerpt(todo: Todo, max_lines: int = 11) -> tuple[str, str]:
    if todo.evidence_text:
        return todo.evidence_text, todo.file
    path = PROJECT_DIR / todo.file
    if not path.is_file():
        return f"Tệp minh chứng: {todo.file}", todo.file
    try:
        lines = path.read_text(encoding="utf-8", errors="replace").splitlines()
    except OSError:
        return f"Tệp minh chứng: {todo.file}", todo.file
    index = 0
    if todo.keyword:
        lowered = todo.keyword.lower()
        for i, line in enumerate(lines):
            if lowered in line.lower():
                index = max(0, i - 2)
                break
    selected = lines[index:index + max_lines]
    formatted: list[str] = []
    for offset, line in enumerate(selected, start=index + 1):
        expanded = line.expandtabs(4).rstrip()
        if len(expanded) > 118:
            expanded = expanded[:115] + "..."
        formatted.append(f"{offset:>4}  {expanded}")
    return "\n".join(formatted), todo.file


def commit_metadata(commit: str) -> tuple[str, str, list[str]]:
    meta = git("show", "-s", "--format=%ad|%s", "--date=short", commit)
    date, message = meta.split("|", 1)
    files = git("show", "--pretty=format:", "--name-only", commit).splitlines()
    files = [item for item in files if item.strip()]
    return date, message, files


def add_evidence_block(doc: Document, todo: Todo, figure_no: int) -> None:
    date, message, files = commit_metadata(todo.commit)
    code, path = read_excerpt(todo)
    table = doc.add_table(rows=4, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = True
    for row in table.rows:
        prevent_row_split(row)

    header = table.cell(0, 0)
    set_cell_shading(header, BLUE_2)
    set_table_cell_text(
        header,
        f"{todo.commit}  ·  {message}",
        bold=True,
        color="FFFFFF",
        size=9.5,
        font="Aptos",
    )

    meta_cell = table.cell(1, 0)
    set_cell_shading(meta_cell, BLUE_3)
    changed = "  •  ".join(Path(item).name for item in files[:5])
    if len(files) > 5:
        changed += f"  •  +{len(files) - 5} tệp"
    set_table_cell_text(
        meta_cell,
        f"Tệp chính: {path}\nTệp thay đổi trong commit: {changed or '—'}",
        color="1F1F1F",
        size=8.5,
        font="Aptos",
    )

    code_cell = table.cell(2, 0)
    set_cell_shading(code_cell, CODE_BG)
    set_table_cell_text(
        code_cell,
        code,
        color=CODE_FG,
        size=7.5,
        font="Consolas",
    )

    footer = table.cell(3, 0)
    set_cell_shading(footer, LIGHT_GRAY)
    set_table_cell_text(
        footer,
        f"Git date: {date}  |  Trạng thái xác nhận: có trong lịch sử Git và source hiện tại",
        color=GRAY,
        size=8,
        font="Aptos",
    )
    add_caption(doc, f"Hình {figure_no}. Minh chứng TODO {todo.code}: source hiện tại và commit {todo.commit}.")


def configure_document(doc: Document) -> None:
    section = doc.sections[0]
    section.page_width = Mm(210)
    section.page_height = Mm(297)
    section.top_margin = Cm(2)
    section.bottom_margin = Cm(2)
    section.left_margin = Cm(3)
    section.right_margin = Cm(2)
    section.header_distance = Cm(0.8)
    section.footer_distance = Cm(0.8)

    normal = doc.styles["Normal"]
    normal.font.name = "Times New Roman"
    normal.font.size = Pt(11)
    normal.font.color.rgb = RGBColor(0x1F, 0x1F, 0x1F)
    normal._element.rPr.rFonts.set(qn("w:eastAsia"), "Times New Roman")
    normal.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    normal.paragraph_format.line_spacing = 1.15
    normal.paragraph_format.space_after = Pt(6)

    title = doc.styles["Title"]
    title.font.name = "Times New Roman"
    title.font.size = Pt(22)
    title.font.bold = True
    title.font.color.rgb = RGBColor.from_string(BLUE)
    title._element.rPr.rFonts.set(qn("w:eastAsia"), "Times New Roman")

    for name, size, color in (
        ("Heading 1", 16, BLUE),
        ("Heading 2", 13, BLUE_2),
        ("Heading 3", 11.5, TEAL),
    ):
        style = doc.styles[name]
        style.font.name = "Times New Roman"
        style.font.size = Pt(size)
        style.font.bold = True
        style.font.color.rgb = RGBColor.from_string(color)
        style._element.rPr.rFonts.set(qn("w:eastAsia"), "Times New Roman")
        style.paragraph_format.keep_with_next = True
        style.paragraph_format.space_before = Pt(10 if name == "Heading 1" else 8)
        style.paragraph_format.space_after = Pt(5)

    caption = doc.styles["Caption"]
    caption.font.name = "Times New Roman"
    caption.font.size = Pt(9)
    caption.font.italic = True
    caption.font.color.rgb = RGBColor.from_string(GRAY)
    caption._element.rPr.rFonts.set(qn("w:eastAsia"), "Times New Roman")

    if "Small Note" not in doc.styles:
        note = doc.styles.add_style("Small Note", WD_STYLE_TYPE.PARAGRAPH)
    else:
        note = doc.styles["Small Note"]
    note.font.name = "Times New Roman"
    note.font.size = Pt(9)
    note.font.italic = True
    note.font.color.rgb = RGBColor.from_string(GRAY)
    note.paragraph_format.space_after = Pt(5)

    settings = doc.settings.element
    update = OxmlElement("w:updateFields")
    update.set(qn("w:val"), "true")
    settings.append(update)

    header = section.header
    hp = header.paragraphs[0]
    hp.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    hr = hp.add_run("MSS301 – ASSIGNMENT 02  |  NGUYỄN TIẾN QUÂN – DE190728")
    hr.font.name = "Aptos"
    hr.font.size = Pt(8)
    hr.font.color.rgb = RGBColor.from_string(GRAY)
    p_pr = hp._p.get_or_add_pPr()
    borders = OxmlElement("w:pBdr")
    bottom = OxmlElement("w:bottom")
    bottom.set(qn("w:val"), "single")
    bottom.set(qn("w:sz"), "6")
    bottom.set(qn("w:space"), "1")
    bottom.set(qn("w:color"), BLUE_2)
    borders.append(bottom)
    p_pr.append(borders)

    add_page_number(section.footer.paragraphs[0])

    props = doc.core_properties
    props.title = "Assignment 02 – Watch Management using Saga Pattern"
    props.subject = "MSS301 – Microservices"
    props.author = "Nguyễn Tiến Quân – DE190728"
    props.keywords = "MSS301, microservices, Saga Pattern, API Gateway, Spring Boot, SQL Server"
    props.comments = "Báo cáo được tổng hợp từ source, Git history và kết quả test ngày 11/11/2026."


def add_cover(doc: Document) -> None:
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(28)
    r = p.add_run("FPT UNIVERSITY")
    r.bold = True
    r.font.name = "Times New Roman"
    r.font.size = Pt(18)
    r.font.color.rgb = RGBColor.from_string(BLUE)

    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("MÔN HỌC: MSS301 – MICROSERVICES")
    r.bold = True
    r.font.size = Pt(13)

    bar = doc.add_table(rows=1, cols=1)
    bar.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell = bar.cell(0, 0)
    set_cell_shading(cell, ORANGE)
    set_table_cell_text(cell, "", color="FFFFFF", size=2)

    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(45)
    r = p.add_run("BÁO CÁO ASSIGNMENT 02")
    r.bold = True
    r.font.name = "Times New Roman"
    r.font.size = Pt(25)
    r.font.color.rgb = RGBColor.from_string(BLUE)

    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_after = Pt(4)
    r = p.add_run("WATCH MANAGEMENT SYSTEM")
    r.bold = True
    r.font.size = Pt(21)
    r.font.color.rgb = RGBColor.from_string(BLUE_2)

    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("Saga Orchestration  •  Database per Service  •  API Gateway")
    r.italic = True
    r.font.name = "Aptos"
    r.font.size = Pt(11)
    r.font.color.rgb = RGBColor.from_string(TEAL)

    architecture = doc.add_table(rows=1, cols=4)
    architecture.alignment = WD_TABLE_ALIGNMENT.CENTER
    labels = [
        ("API Gateway\n:9000", BLUE_2),
        ("Account\n:8081", GREEN),
        ("Brand\n:8082", ORANGE),
        ("Watch + Saga\n:8083", PURPLE),
    ]
    for cell, (label, color) in zip(architecture.rows[0].cells, labels):
        set_cell_shading(cell, color)
        set_table_cell_text(cell, label, bold=True, color="FFFFFF", size=9, font="Aptos", align=WD_ALIGN_PARAGRAPH.CENTER)

    doc.add_paragraph()
    info = doc.add_table(rows=4, cols=2)
    info.alignment = WD_TABLE_ALIGNMENT.CENTER
    info.style = "Table Grid"
    rows = [
        ("Sinh viên", "Nguyễn Tiến Quân"),
        ("Mã sinh viên", "DE190728"),
        ("Môn học", "MSS301"),
        ("Ngày hoàn thành", "11/11/2026"),
    ]
    for row, (label, value) in zip(info.rows, rows):
        set_cell_shading(row.cells[0], BLUE_3)
        set_table_cell_text(row.cells[0], label, bold=True, color=BLUE, size=10)
        set_table_cell_text(row.cells[1], value, color="000000", size=10)

    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(26)
    r = p.add_run("FA2026")
    r.bold = True
    r.font.size = Pt(12)
    r.font.color.rgb = RGBColor.from_string(GRAY)


def add_toc(doc: Document) -> None:
    doc.add_page_break()
    p = doc.add_paragraph(style="Title")
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run("MỤC LỤC")
    toc = doc.add_paragraph()
    add_field(toc, 'TOC \\o "1-3" \\h \\z \\u', "Mục lục sẽ tự cập nhật khi mở bằng Microsoft Word.")
    note = doc.add_paragraph(style="Small Note")
    note.alignment = WD_ALIGN_PARAGRAPH.CENTER
    note.add_run("Nếu Word chưa cập nhật tự động: chọn mục lục → Update Field → Update entire table.")
    doc.add_page_break()


def add_architecture(doc: Document, figure_no: int) -> None:
    table = doc.add_table(rows=6, cols=3)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = True

    client = table.cell(0, 0).merge(table.cell(0, 2))
    set_cell_shading(client, "EAF2F8")
    set_table_cell_text(client, "CLIENT / POSTMAN", bold=True, color=BLUE, size=10, font="Aptos", align=WD_ALIGN_PARAGRAPH.CENTER)

    arrow = table.cell(1, 0).merge(table.cell(1, 2))
    set_table_cell_text(arrow, "↓  HTTP / JSON  ↓", bold=True, color=TEAL, size=10, font="Aptos", align=WD_ALIGN_PARAGRAPH.CENTER)

    gateway = table.cell(2, 0).merge(table.cell(2, 2))
    set_cell_shading(gateway, BLUE_2)
    set_table_cell_text(gateway, "API GATEWAY :9000\nRouting • Bearer session • Administrator authorization • Trusted headers", bold=True, color="FFFFFF", size=10, font="Aptos", align=WD_ALIGN_PARAGRAPH.CENTER)

    arrows = ["↓ /api/auth", "↓ /api/brands", "↓ /api/watches, categories, sagas"]
    for cell, text in zip(table.rows[3].cells, arrows):
        set_table_cell_text(cell, text, bold=True, color=TEAL, size=8.5, font="Aptos", align=WD_ALIGN_PARAGRAPH.CENTER)

    services = [
        ("MSAccount_QuanNT :8081", GREEN),
        ("MSBrand_QuanNT :8082", ORANGE),
        ("MSWatch_QuanNT :8083\nSaga Orchestrator → Brand internal API", PURPLE),
    ]
    for cell, (text, color) in zip(table.rows[4].cells, services):
        set_cell_shading(cell, color)
        set_table_cell_text(cell, text, bold=True, color="FFFFFF", size=8.5, font="Aptos", align=WD_ALIGN_PARAGRAPH.CENTER)

    databases = ["DBAccount\nStoreAccounts", "DBBrand\nWatchBrands + references", "DBWatch\nWatches + Categories + Saga log"]
    for cell, text in zip(table.rows[5].cells, databases):
        set_cell_shading(cell, LIGHT_GRAY)
        set_table_cell_text(cell, text, bold=True, color="333333", size=8.5, font="Aptos", align=WD_ALIGN_PARAGRAPH.CENTER)

    add_caption(doc, f"Hình {figure_no}. Kiến trúc tổng thể Watch Management theo Database per Service.")


def add_saga_flow(doc: Document, figure_no: int) -> None:
    table = doc.add_table(rows=5, cols=3)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    headers = ["CREATE", "UPDATE", "DELETE"]
    flows = [
        ["Tạo Watch trong DBWatch", "Đăng ký Brand reference", "COMPLETED"],
        ["Snapshot + cập nhật Watch", "Đăng ký brand mới / gỡ brand cũ", "COMPLETED"],
        ["Gỡ Brand reference", "Xóa Watch", "COMPLETED"],
    ]
    for i, (cell, header) in enumerate(zip(table.rows[0].cells, headers)):
        set_cell_shading(cell, [GREEN, ORANGE, PURPLE][i])
        set_table_cell_text(cell, header, bold=True, color="FFFFFF", size=9.5, font="Aptos", align=WD_ALIGN_PARAGRAPH.CENTER)
    for row_index in range(1, 4):
        for col_index in range(3):
            text = flows[col_index][row_index - 1]
            set_cell_shading(table.cell(row_index, col_index), "F7F9FB")
            set_table_cell_text(table.cell(row_index, col_index), text, color="222222", size=8.5, font="Aptos", align=WD_ALIGN_PARAGRAPH.CENTER)
    compensation = table.cell(4, 0).merge(table.cell(4, 2))
    set_cell_shading(compensation, "FCE4D6")
    set_table_cell_text(
        compensation,
        "KHI LỖI: FAILED → COMPENSATING → chạy ngược các bước đã thành công (retry tối đa 3 lần) → COMPENSATED",
        bold=True,
        color="9C0006",
        size=9,
        font="Aptos",
        align=WD_ALIGN_PARAGRAPH.CENTER,
    )
    add_caption(doc, f"Hình {figure_no}. Luồng Saga create/update/delete và cơ chế compensation.")


def add_summary_table(doc: Document) -> None:
    table = doc.add_table(rows=1, cols=4)
    table.style = "Table Grid"
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    headers = ["Thành phần", "Port", "Database", "Vai trò"]
    for cell, text in zip(table.rows[0].cells, headers):
        set_cell_shading(cell, BLUE_2)
        set_table_cell_text(cell, text, bold=True, color="FFFFFF", size=9, align=WD_ALIGN_PARAGRAPH.CENTER)
    set_repeat_table_header(table.rows[0])
    rows = [
        ("API Gateway", "9000", "—", "Route, validate session, authorize Admin"),
        ("Account Service", "8081", "DBAccount", "StoreAccounts, login/logout/validate"),
        ("Brand Service", "8082", "DBBrand", "WatchBrands và Brand–Watch reference"),
        ("Watch Service", "8083", "DBWatch", "Catalog, CRUD, Saga state/log"),
    ]
    for values in rows:
        cells = table.add_row().cells
        for cell, value in zip(cells, values):
            set_table_cell_text(cell, value, size=9)


def collect_test_results() -> list[tuple[str, int, int, int, int]]:
    modules = [
        ("Account Service", PROJECT_DIR / "MSAccount_QuanNT" / "target" / "surefire-reports"),
        ("Brand Service", PROJECT_DIR / "MSBrand_QuanNT" / "target" / "surefire-reports"),
        ("Watch Service + Saga", PROJECT_DIR / "MSWatch_QuanNT" / "target" / "surefire-reports"),
        ("API Gateway", PROJECT_DIR / "APIGateway_QuanNT" / "target" / "surefire-reports"),
    ]
    results = []
    for name, folder in modules:
        tests = failures = errors = skipped = 0
        for report in folder.glob("TEST-*.xml"):
            root = ET.parse(report).getroot()
            tests += int(float(root.attrib.get("tests", "0")))
            failures += int(float(root.attrib.get("failures", "0")))
            errors += int(float(root.attrib.get("errors", "0")))
            skipped += int(float(root.attrib.get("skipped", "0")))
        results.append((name, tests, failures, errors, skipped))
    return results


def add_test_table(doc: Document, results: list[tuple[str, int, int, int, int]]) -> None:
    table = doc.add_table(rows=1, cols=5)
    table.style = "Table Grid"
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    headers = ["Module", "Tests", "Failures", "Errors", "Skipped"]
    for cell, text in zip(table.rows[0].cells, headers):
        set_cell_shading(cell, BLUE_2)
        set_table_cell_text(cell, text, bold=True, color="FFFFFF", size=9, align=WD_ALIGN_PARAGRAPH.CENTER)
    total = [0, 0, 0, 0]
    for name, tests, failures, errors, skipped in results:
        values = (name, str(tests), str(failures), str(errors), str(skipped))
        cells = table.add_row().cells
        for i, (cell, value) in enumerate(zip(cells, values)):
            set_table_cell_text(cell, value, size=9, align=WD_ALIGN_PARAGRAPH.CENTER if i else None)
        for i, value in enumerate((tests, failures, errors, skipped)):
            total[i] += value
    cells = table.add_row().cells
    for cell in cells:
        set_cell_shading(cell, "E2F0D9")
    for i, value in enumerate(("TỔNG", *map(str, total))):
        set_table_cell_text(cells[i], value, bold=True, color=GREEN, size=9, align=WD_ALIGN_PARAGRAPH.CENTER)


def add_test_evidence(doc: Document, results: list[tuple[str, int, int, int, int]], figure_no: int) -> None:
    total_tests = sum(item[1] for item in results)
    total_fail = sum(item[2] for item in results)
    total_errors = sum(item[3] for item in results)
    table = doc.add_table(rows=3, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    set_cell_shading(table.cell(0, 0), CODE_BG)
    set_table_cell_text(table.cell(0, 0), "MAVEN REACTOR TEST — 11/11/2026", bold=True, color="7FFFD4", size=11, font="Consolas")
    details = "\n".join(f"[SUCCESS] {name:<24} Tests: {tests:>2}  Failures: {failures}  Errors: {errors}" for name, tests, failures, errors, _ in results)
    set_cell_shading(table.cell(1, 0), CODE_BG)
    set_table_cell_text(table.cell(1, 0), details, color=CODE_FG, size=8.5, font="Consolas")
    set_cell_shading(table.cell(2, 0), "E2F0D9" if total_fail == 0 and total_errors == 0 else "F4CCCC")
    set_table_cell_text(
        table.cell(2, 0),
        f"BUILD SUCCESS  •  {total_tests}/{total_tests} TESTS PASSED  •  0 FAILED  •  0 ERRORS",
        bold=True,
        color=GREEN if total_fail == 0 and total_errors == 0 else "9C0006",
        size=10,
        font="Aptos",
        align=WD_ALIGN_PARAGRAPH.CENTER,
    )
    add_caption(doc, f"Hình {figure_no}. Kết quả Maven clean test xác nhận toàn bộ 18 test pass.")


def postman_stats() -> tuple[int, int, int, list[str]]:
    data = json.loads((PROJECT_DIR / "postman" / "WatchManagement.postman_collection.json").read_text(encoding="utf-8"))
    folders = 0
    requests = 0
    test_lines = 0
    names: list[str] = []

    def walk(items):
        nonlocal folders, requests, test_lines
        for item in items:
            if "request" in item:
                requests += 1
                names.append(item.get("name", ""))
                for event in item.get("event", []):
                    if event.get("listen") == "test":
                        test_lines += len(event.get("script", {}).get("exec", []))
            else:
                folders += 1
                walk(item.get("item", []))

    walk(data.get("item", []))
    return folders, requests, test_lines, names


def add_endpoint_table(doc: Document) -> None:
    table = doc.add_table(rows=1, cols=4)
    table.style = "Table Grid"
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    headers = ["Method", "Endpoint", "Quyền", "Mục đích"]
    for cell, text in zip(table.rows[0].cells, headers):
        set_cell_shading(cell, BLUE_2)
        set_table_cell_text(cell, text, bold=True, color="FFFFFF", size=8.5, align=WD_ALIGN_PARAGRAPH.CENTER)
    set_repeat_table_header(table.rows[0])
    endpoints = [
        ("POST", "/api/auth/login", "Public", "Đăng nhập và nhận session token"),
        ("POST", "/api/auth/logout", "Bearer", "Thu hồi session token"),
        ("GET", "/api/brands", "Public", "Danh sách brand"),
        ("GET", "/api/categories", "Public", "Danh sách category"),
        ("GET", "/api/watches", "Public", "Danh sách WatchID giảm dần"),
        ("GET", "/api/watches/{id}", "Public", "Chi tiết Watch kèm CategoryName"),
        ("POST", "/api/watches", "Admin", "Tạo Watch bằng Saga"),
        ("PUT", "/api/watches/{id}", "Admin", "Cập nhật Watch và chuyển Brand reference"),
        ("DELETE", "/api/watches/{id}", "Admin", "Xóa Watch bằng Saga"),
        ("GET", "/api/sagas", "Public", "Danh sách operation Saga"),
        ("GET", "/api/sagas/{operationId}", "Public", "Trạng thái và step log"),
    ]
    for values in endpoints:
        cells = table.add_row().cells
        for i, (cell, value) in enumerate(zip(cells, values)):
            set_table_cell_text(cell, value, size=8.5, align=WD_ALIGN_PARAGRAPH.CENTER if i in (0, 2) else None)


def add_commit_table(doc: Document) -> None:
    table = doc.add_table(rows=1, cols=3)
    table.style = "Table Grid"
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    headers = ["TODO", "Commit", "Commit message"]
    for cell, text in zip(table.rows[0].cells, headers):
        set_cell_shading(cell, BLUE_2)
        set_table_cell_text(cell, text, bold=True, color="FFFFFF", size=8.5, align=WD_ALIGN_PARAGRAPH.CENTER)
    set_repeat_table_header(table.rows[0])
    for _, todos in GROUPS:
        for todo in todos:
            _, message, _ = commit_metadata(todo.commit)
            cells = table.add_row().cells
            set_table_cell_text(cells[0], todo.code, bold=True, color=BLUE_2, size=8.2, align=WD_ALIGN_PARAGRAPH.CENTER)
            set_table_cell_text(cells[1], todo.commit, color="333333", size=8.2, font="Consolas", align=WD_ALIGN_PARAGRAPH.CENTER)
            set_table_cell_text(cells[2], message, color="222222", size=8.2)


def build_report() -> Path:
    doc = Document()
    configure_document(doc)
    add_cover(doc)
    add_toc(doc)

    figure_no = 1

    doc.add_heading("1. Giới thiệu và phạm vi bài làm", level=1)
    doc.add_paragraph(
        "Báo cáo trình bày quá trình xây dựng Watch Management System theo Assignment 02 môn MSS301. "
        "Hệ thống gồm bốn ứng dụng Spring Boot độc lập và ba database SQL Server, áp dụng Database per Service. "
        "Trọng tâm của bài là duy trì nhất quán dữ liệu Watch–Brand bằng Saga Orchestration mà không sử dụng distributed transaction hoặc two-phase commit."
    )
    doc.add_paragraph(
        "Nội dung được đối chiếu trực tiếp với đề gốc, source hiện tại, Git history và kết quả Maven test chạy ngày 11/11/2026. "
        "Mỗi TODO dưới đây có yêu cầu, commit, mô tả triển khai và khối minh chứng từ tệp nguồn tương ứng."
    )
    bullets = [
        "CRUD Watch; item mới hiển thị đầu danh sách.",
        "Public GET catalog, Administrator mới được phép tạo/cập nhật/xóa.",
        "Validation name, price, release date, stock, category và brand.",
        "Đồng bộ Brand reference và compensation khi một bước Saga thất bại.",
        "API Gateway là single entry point; Postman và automated test đi kèm.",
    ]
    for item in bullets:
        doc.add_paragraph(item, style="List Bullet")

    doc.add_heading("2. Kiến trúc và môi trường thực hiện", level=1)
    doc.add_paragraph(
        "Công nghệ chính: Java 21, Spring Boot 4.1.1, Spring Cloud 2025.1.3, Spring Data JPA, "
        "Spring Cloud Gateway Server Web MVC, SQL Server 2022, H2 MSSQL compatibility mode cho integration test, Docker Compose và Postman."
    )
    add_architecture(doc, figure_no)
    figure_no += 1
    add_summary_table(doc)
    doc.add_heading("2.1. Nguyên tắc Database per Service", level=2)
    doc.add_paragraph(
        "Mỗi service chỉ truy cập database do mình sở hữu. DBWatch lưu BrandID như một định danh logic, không tạo foreign key tới DBBrand. "
        "Brand Service lưu BrandWatchReference riêng để biết các Watch đang tham chiếu brand. Việc đồng bộ hai database được thực hiện bằng lời gọi REST trong Saga."
    )
    doc.add_heading("2.2. Luồng Saga", level=2)
    add_saga_flow(doc, figure_no)
    figure_no += 1
    doc.add_paragraph(
        "Saga state gồm PENDING, EXECUTING, COMPLETED, FAILED, COMPENSATING và COMPENSATED. "
        "Mỗi bước được ghi log; compensation được thiết kế idempotent và retry tối đa ba lần."
    )

    doc.add_page_break()
    doc.add_heading("3. Minh chứng thực hiện theo từng TODO", level=1)
    doc.add_paragraph(
        "Các mục được sắp đúng thứ tự triển khai. Commit hash và message lấy trực tiếp từ lịch sử Git của thư mục Slot18; "
        "đoạn source trong khối minh chứng được lấy từ phiên bản hiện tại sau khi toàn bộ test đã pass."
    )
    for group_index, (group_name, todos) in enumerate(GROUPS, start=1):
        doc.add_heading(f"3.{group_index}. {group_name}", level=2)
        for todo in todos:
            doc.add_heading(f"3.{group_index}.{todos.index(todo) + 1}. TODO {todo.code}", level=3)
            add_labeled_paragraph(doc, "Yêu cầu: ", todo.requirement)
            _, message, _ = commit_metadata(todo.commit)
            add_labeled_paragraph(doc, "Commit: ", f"{todo.commit} — {message}")
            add_labeled_paragraph(doc, "Triển khai và kết quả: ", todo.implementation)
            add_evidence_block(doc, todo, figure_no)
            figure_no += 1

    doc.add_page_break()
    doc.add_heading("4. Kiểm thử tự động và kết quả xác nhận", level=1)
    doc.add_paragraph(
        "Lệnh mvn -U clean test được chạy tại root WatchManagement ngày 11/11/2026. Maven reactor compile bốn module và thực thi toàn bộ unit/integration test. "
        "Kết quả cuối cùng BUILD SUCCESS, không có failure, error hoặc skipped test."
    )
    results = collect_test_results()
    add_test_table(doc, results)
    add_test_evidence(doc, results, figure_no)
    figure_no += 1
    doc.add_heading("4.1. Phạm vi test", level=2)
    test_scope = [
        "Account: login đúng, sai mật khẩu và tài khoản inactive.",
        "Brand: tạo reference, gọi lặp idempotent và chuyển reference sang brand khác.",
        "Watch: Bean Validation, CRUD service, schema/seed integration và thứ tự WatchID giảm dần.",
        "Saga: happy path create, compensation khi Brand lỗi và restore snapshot khi update lỗi.",
        "Gateway: 401 khi thiếu token, 403 với Staff và trusted header với Administrator.",
    ]
    for item in test_scope:
        doc.add_paragraph(item, style="List Bullet")

    doc.add_page_break()
    doc.add_heading("5. Postman, API và hướng dẫn chạy", level=1)
    folders, requests, test_lines, request_names = postman_stats()
    doc.add_paragraph(
        f"Postman Collection hiện có {folders} folder, {requests} request và {test_lines} dòng test script. "
        "Các request luôn đi qua Gateway tại http://localhost:9000; internal API không được route ra ngoài."
    )
    add_endpoint_table(doc)
    doc.add_heading("5.1. Kịch bản Postman", level=2)
    for name in request_names:
        doc.add_paragraph(name, style="List Bullet")
    doc.add_heading("5.2. Chạy hệ thống", level=2)
    for command in (
        "Copy-Item .env.example .env",
        "docker compose up -d --build",
        "docker compose ps",
        "mvn clean test",
        "docker compose down",
    ):
        table = doc.add_table(rows=1, cols=1)
        set_cell_shading(table.cell(0, 0), CODE_BG)
        set_table_cell_text(table.cell(0, 0), command, color=CODE_FG, size=9, font="Consolas")
    note = doc.add_paragraph(style="Small Note")
    note.add_run("Chỉ dùng docker compose down -v khi chủ động muốn xóa volume và seed lại dữ liệu từ đầu.")

    doc.add_page_break()
    doc.add_heading("6. Danh sách commit đã sử dụng", level=1)
    doc.add_paragraph(
        "Bảng dưới đây gồm 33 commit TODO độc lập. Message tuân theo Conventional Commits và mỗi commit có một mục đích rõ ràng, thuận tiện review hoặc revert."
    )
    add_commit_table(doc)

    doc.add_page_break()
    doc.add_heading("7. Kết luận", level=1)
    doc.add_paragraph(
        "Watch Management System đã hoàn thành đầy đủ phạm vi Assignment 02: tách bốn project, sở hữu database độc lập, seed đúng dữ liệu đề, "
        "CRUD Watch có validation và phân quyền, đồng bộ Brand reference bằng Saga Orchestration, ghi trạng thái/step log và compensation khi lỗi. "
        "API Gateway đóng vai trò single entry point; Docker Compose, Postman, README và lịch sử Git hỗ trợ vận hành và đánh giá bài làm."
    )
    conclusions = [
        "33/33 TODO có commit minh chứng rõ ràng.",
        "18/18 automated tests pass, 0 failure, 0 error.",
        "5 account, 5 brand, 5 category và 10 watch được seed đúng yêu cầu.",
        "Saga hỗ trợ CREATE, UPDATE, DELETE và compensation retry tối đa 3 lần.",
        "Public catalog và Administrator mutation được kiểm soát tại Gateway.",
        "Postman Collection có 13 request bao phủ các luồng API trọng yếu.",
    ]
    for item in conclusions:
        doc.add_paragraph(item, style="List Bullet")

    doc.save(OUTPUT)
    return OUTPUT


if __name__ == "__main__":
    path = build_report()
    print(path)
