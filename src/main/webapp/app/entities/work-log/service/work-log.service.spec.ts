import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IWorkLog } from '../work-log.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../work-log.test-samples';

import { WorkLogService, RestWorkLog } from './work-log.service';

const requireRestSample: RestWorkLog = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
};

describe('WorkLog Service', () => {
  let service: WorkLogService;
  let httpMock: HttpTestingController;
  let expectedResult: IWorkLog | IWorkLog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(WorkLogService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a WorkLog', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const workLog = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(workLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a WorkLog', () => {
      const workLog = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(workLog).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a WorkLog', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of WorkLog', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a WorkLog', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addWorkLogToCollectionIfMissing', () => {
      it('should add a WorkLog to an empty array', () => {
        const workLog: IWorkLog = sampleWithRequiredData;
        expectedResult = service.addWorkLogToCollectionIfMissing([], workLog);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(workLog);
      });

      it('should not add a WorkLog to an array that contains it', () => {
        const workLog: IWorkLog = sampleWithRequiredData;
        const workLogCollection: IWorkLog[] = [
          {
            ...workLog,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addWorkLogToCollectionIfMissing(workLogCollection, workLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a WorkLog to an array that doesn't contain it", () => {
        const workLog: IWorkLog = sampleWithRequiredData;
        const workLogCollection: IWorkLog[] = [sampleWithPartialData];
        expectedResult = service.addWorkLogToCollectionIfMissing(workLogCollection, workLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(workLog);
      });

      it('should add only unique WorkLog to an array', () => {
        const workLogArray: IWorkLog[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const workLogCollection: IWorkLog[] = [sampleWithRequiredData];
        expectedResult = service.addWorkLogToCollectionIfMissing(workLogCollection, ...workLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const workLog: IWorkLog = sampleWithRequiredData;
        const workLog2: IWorkLog = sampleWithPartialData;
        expectedResult = service.addWorkLogToCollectionIfMissing([], workLog, workLog2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(workLog);
        expect(expectedResult).toContain(workLog2);
      });

      it('should accept null and undefined values', () => {
        const workLog: IWorkLog = sampleWithRequiredData;
        expectedResult = service.addWorkLogToCollectionIfMissing([], null, workLog, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(workLog);
      });

      it('should return initial array if no WorkLog is added', () => {
        const workLogCollection: IWorkLog[] = [sampleWithRequiredData];
        expectedResult = service.addWorkLogToCollectionIfMissing(workLogCollection, undefined, null);
        expect(expectedResult).toEqual(workLogCollection);
      });
    });

    describe('compareWorkLog', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareWorkLog(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareWorkLog(entity1, entity2);
        const compareResult2 = service.compareWorkLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareWorkLog(entity1, entity2);
        const compareResult2 = service.compareWorkLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareWorkLog(entity1, entity2);
        const compareResult2 = service.compareWorkLog(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
