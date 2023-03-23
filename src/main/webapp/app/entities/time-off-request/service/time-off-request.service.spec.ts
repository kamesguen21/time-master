import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITimeOffRequest } from '../time-off-request.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../time-off-request.test-samples';

import { TimeOffRequestService, RestTimeOffRequest } from './time-off-request.service';

const requireRestSample: RestTimeOffRequest = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.toJSON(),
  endDate: sampleWithRequiredData.endDate?.toJSON(),
};

describe('TimeOffRequest Service', () => {
  let service: TimeOffRequestService;
  let httpMock: HttpTestingController;
  let expectedResult: ITimeOffRequest | ITimeOffRequest[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TimeOffRequestService);
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

    it('should create a TimeOffRequest', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const timeOffRequest = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(timeOffRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TimeOffRequest', () => {
      const timeOffRequest = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(timeOffRequest).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TimeOffRequest', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TimeOffRequest', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TimeOffRequest', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTimeOffRequestToCollectionIfMissing', () => {
      it('should add a TimeOffRequest to an empty array', () => {
        const timeOffRequest: ITimeOffRequest = sampleWithRequiredData;
        expectedResult = service.addTimeOffRequestToCollectionIfMissing([], timeOffRequest);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(timeOffRequest);
      });

      it('should not add a TimeOffRequest to an array that contains it', () => {
        const timeOffRequest: ITimeOffRequest = sampleWithRequiredData;
        const timeOffRequestCollection: ITimeOffRequest[] = [
          {
            ...timeOffRequest,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTimeOffRequestToCollectionIfMissing(timeOffRequestCollection, timeOffRequest);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TimeOffRequest to an array that doesn't contain it", () => {
        const timeOffRequest: ITimeOffRequest = sampleWithRequiredData;
        const timeOffRequestCollection: ITimeOffRequest[] = [sampleWithPartialData];
        expectedResult = service.addTimeOffRequestToCollectionIfMissing(timeOffRequestCollection, timeOffRequest);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(timeOffRequest);
      });

      it('should add only unique TimeOffRequest to an array', () => {
        const timeOffRequestArray: ITimeOffRequest[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const timeOffRequestCollection: ITimeOffRequest[] = [sampleWithRequiredData];
        expectedResult = service.addTimeOffRequestToCollectionIfMissing(timeOffRequestCollection, ...timeOffRequestArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const timeOffRequest: ITimeOffRequest = sampleWithRequiredData;
        const timeOffRequest2: ITimeOffRequest = sampleWithPartialData;
        expectedResult = service.addTimeOffRequestToCollectionIfMissing([], timeOffRequest, timeOffRequest2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(timeOffRequest);
        expect(expectedResult).toContain(timeOffRequest2);
      });

      it('should accept null and undefined values', () => {
        const timeOffRequest: ITimeOffRequest = sampleWithRequiredData;
        expectedResult = service.addTimeOffRequestToCollectionIfMissing([], null, timeOffRequest, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(timeOffRequest);
      });

      it('should return initial array if no TimeOffRequest is added', () => {
        const timeOffRequestCollection: ITimeOffRequest[] = [sampleWithRequiredData];
        expectedResult = service.addTimeOffRequestToCollectionIfMissing(timeOffRequestCollection, undefined, null);
        expect(expectedResult).toEqual(timeOffRequestCollection);
      });
    });

    describe('compareTimeOffRequest', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTimeOffRequest(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTimeOffRequest(entity1, entity2);
        const compareResult2 = service.compareTimeOffRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTimeOffRequest(entity1, entity2);
        const compareResult2 = service.compareTimeOffRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTimeOffRequest(entity1, entity2);
        const compareResult2 = service.compareTimeOffRequest(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
