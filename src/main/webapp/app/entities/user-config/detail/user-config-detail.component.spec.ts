import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserConfigDetailComponent } from './user-config-detail.component';

describe('UserConfig Management Detail Component', () => {
  let comp: UserConfigDetailComponent;
  let fixture: ComponentFixture<UserConfigDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserConfigDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userConfig: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserConfigDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserConfigDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userConfig on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userConfig).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
