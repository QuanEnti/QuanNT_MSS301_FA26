# Chạy kiểm thử Postman

1. Khởi động hệ thống bằng `docker-compose up -d --build` trong thư mục `Slot13`.
2. Import `Saga_Postman_Collection.json` vào Postman Desktop.
3. Mở Collection Runner và chọn theo đúng thứ tự:
   `0 → KB1 → KB2 → KB3 → KB4 → KB5 → KB7`.
4. Bỏ chọn folder `Thủ công (không chạy bằng Runner)` rồi chạy; tất cả test phải xanh.
5. Chạy KB6 thủ công: snapshot → `docker-compose stop payment-service` → đặt hàng →
   `docker-compose start payment-service` → kiểm tra kho và saga logs.
6. KB8 là kịch bản tuỳ chọn; hướng dẫn thao tác nằm trong description của từng request.

Có thể chạy phần tự động bằng Newman:

```powershell
npx newman run .\Saga_Postman_Collection.json `
  --folder "0 - Kiểm tra service" `
  --folder "KB1 - Happy path" `
  --folder "KB2 - Thanh toán thất bại (402)" `
  --folder "KB3 - Hết hàng (409)" `
  --folder "KB4 - Sản phẩm không tồn tại (404 inventory)" `
  --folder "KB5 - Ví không tồn tại (404 payment)" `
  --folder "KB7 - Idempotent (gọi lặp lại)"
```
