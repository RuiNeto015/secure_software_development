import { TestBed } from '@angular/core/testing';
import { Message } from 'primeng/api';
import { MessagesService } from './messages.service';

describe('MessagesService', () => {
  let service: MessagesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessagesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should emit success message', () => {
    const message: Message = { severity: 'success', summary: 'Test Title', detail: 'Test Content' };
    const subscription = service.getMessages().subscribe((msg: Message) => {
      expect(msg).toEqual(message);
    });
    service.success('Test Content', 'Test Title');
    subscription.unsubscribe();
  });

  it('should emit error message', () => {
    const message: Message = { severity: 'danger', summary: 'Test Title', detail: 'Test Content' };
    const subscription = service.getMessages().subscribe((msg: Message) => {
      expect(msg).toEqual(message);
    });
    service.error('Test Content', 'Test Title');
    subscription.unsubscribe();
  });

  it('should emit info message', () => {
    const message: Message = { severity: 'info', summary: 'Test Title', detail: 'Test Content' };
    const subscription = service.getMessages().subscribe((msg: Message) => {
      expect(msg).toEqual(message);
    });
    service.info('Test Content', 'Test Title');
    subscription.unsubscribe();
  });

  it('should emit warning message', () => {
    const message: Message = { severity: 'warn', summary: 'Test Title', detail: 'Test Content' };
    const subscription = service.getMessages().subscribe((msg: Message) => {
      expect(msg).toEqual(message);
    });
    service.warn('Test Content', 'Test Title');
    subscription.unsubscribe();
  });

  it('should emit contrast message', () => {
    const message: Message = { severity: 'contrast', summary: 'Test Title', detail: 'Test Content' };
    const subscription = service.getMessages().subscribe((msg: Message) => {
      expect(msg).toEqual(message);
    });
    service.contrast('Test Content', 'Test Title');
    subscription.unsubscribe();
  });
});
