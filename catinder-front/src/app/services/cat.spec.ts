import { TestBed } from '@angular/core/testing';

import { Cat } from './cat';

describe('Cat', () => {
  let service: Cat;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Cat);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
