export interface Notification {

  status?: 'BORROWED' | 'RETURNED' | 'RETURNED_APPROVED';
  message?: string;
  bookTitle?: string;
}
