# InternFlow API Documentation

## Base URL
```
http://localhost:1234/api
```

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## 1. Authentication Endpoints

### POST `/api/auth/login`
Login and get JWT token.

**Request Body:**
```json
{
  "email": "intern@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "intern@example.com",
  "name": "John Doe",
  "role": "INTERN",
  "userId": 1
}
```

---

## 2. Intern Onboarding

### POST `/api/interns/onboard`
Register a new intern (public endpoint).

**Request Body:**
```json
{
  "email": "intern@example.com",
  "password": "password123",
  "name": "John Doe",
  "joiningDate": "2025-05-01",
  "internshipDurationMonths": 6,
  "stipendType": "MONTHLY",
  "stipendAmount": 15000.0,
  "panNumber": "ABCDE1234F",
  "aadhaarNumber": "123456789012",
  "bankAccountNumber": "1234567890",
  "bankIfscCode": "BANK0001234",
  "bankName": "Bank Name",
  "bankBranch": "Branch Name",
  "address": "123 Street",
  "city": "City",
  "state": "State",
  "pincode": "123456"
}
```

---

## 3. Invoice Endpoints

### POST `/api/invoices/generate`
Generate an invoice for the logged-in intern.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "month": 5,
  "year": 2025
}
```

**Response:** Invoice object with calculated values.

### GET `/api/invoices/my-invoices`
Get all invoices for the logged-in intern.

**Headers:** `Authorization: Bearer <token>`

### GET `/api/invoices/all`
Get all invoices (HR only).

### PUT `/api/invoices/{invoiceId}/status`
Update invoice status (HR only).

**Query Parameters:**
- `status`: PENDING, APPROVED, or PAID
- `remarks`: (optional) Remarks

---

## 4. Leave Endpoints

### POST `/api/leaves/request`
Request a leave (logged-in intern).

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "leaveDate": "2025-06-15",
  "reason": "Personal work"
}
```

### GET `/api/leaves/my-leaves`
Get all leaves for the logged-in intern.

**Headers:** `Authorization: Bearer <token>`

### GET `/api/leaves/balance`
Get leave balance for the logged-in intern.

**Headers:** `Authorization: Bearer <token>`

**Response:**
```json
{
  "paidLeavesUsed": 2,
  "paidLeavesRemaining": 4,
  "unpaidLeavesTotal": 1,
  "totalPaidLeavesEarned": 6
}
```

### GET `/api/leaves/pending`
Get all pending leave requests (HR only).

### PUT `/api/leaves/{leaveId}/approve`
Approve a leave request (HR only).

**Query Parameters:**
- `approvedBy`: Name of approver

### PUT `/api/leaves/{leaveId}/reject`
Reject a leave request (HR only).

---

## 5. Announcement Endpoints

### POST `/api/announcements`
Create an announcement (HR only).

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "title": "Office Holiday",
  "body": "Office will be closed on...",
  "expiryDate": "2025-12-31"
}
```

### GET `/api/announcements/active`
Get all active announcements (public).

### GET `/api/announcements/all`
Get all announcements (HR only).

### PUT `/api/announcements/{announcementId}/deactivate`
Deactivate an announcement (HR only).

---

## 6. AI Endpoints

### POST `/api/ai/policy-buddy`
Ask policy questions (logged-in intern).

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "question": "How many paid leaves do I have left?"
}
```

**Response:**
```json
{
  "answer": "Based on your internship details..."
}
```

### GET `/api/ai/hr-summary`
Get AI-generated monthly summary (HR only).

**Query Parameters:**
- `month`: 1-12
- `year`: e.g., 2025

**Response:** Plain text summary.

---

## 7. Test Endpoints

### GET `/api/hello`
Test endpoint to verify API is running.

### GET `/api/health`
Health check endpoint.

---

## Error Responses

All endpoints return standard error format:

```json
{
  "error": "Error message here"
}
```

**Status Codes:**
- `200` - Success
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

---

## Notes

1. **JWT Token Expiration:** Tokens expire after 24 hours (configurable in `application.properties`).

2. **Invoice Number Format:** Generated as 001, 002, 003... based on months since joining.

3. **Leave Policy:** 
   - 1 paid leave per month
   - Can carry forward
   - System automatically determines PAID vs UNPAID based on balance

4. **Stipend Calculation:**
   - Monthly: Deducts only unpaid leaves proportionally
   - Daily: Deducts unpaid leave days from total payable days

5. **Working Days:** Calculated excluding weekends (Saturday & Sunday).

---

## Example Usage with cURL

### Login
```bash
curl -X POST http://localhost:1234/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"intern@example.com","password":"password123"}'
```

### Generate Invoice (with token)
```bash
curl -X POST http://localhost:1234/api/invoices/generate \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"month":5,"year":2025}'
```

### Get Leave Balance
```bash
curl -X GET http://localhost:1234/api/leaves/balance \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

**Happy Coding! 🚀**

