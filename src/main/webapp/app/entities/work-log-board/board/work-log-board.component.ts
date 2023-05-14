import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { combineLatest, filter, Observable, switchMap, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { SortService } from 'app/shared/sort/sort.service';
import { CdkDragDrop, moveItemInArray, transferArrayItem, CdkDropList } from '@angular/cdk/drag-drop';
import { AccountService } from '../../../core/auth/account.service';
import { TicketStatus } from '../../enumerations/ticket-status.model';
import { EntityArrayResponseType, TicketService } from '../../ticket/service/ticket.service';
import { ITicket } from '../../ticket/ticket.model';
import { IWorkLog } from '../../work-log/work-log.model';
import { Board } from '../../work-log/models/board.model';
import { WorkLogService } from '../../work-log/service/work-log.service';
import { WorkLogDeleteDialogComponent } from '../../work-log/delete/work-log-delete-dialog.component';
import { Column } from '../../work-log/models/column.model';
import { TicketDialogComponent } from '../ticket-dialog/ticket-dialog.component';

@Component({
  selector: 'jhi-work-log-board',
  templateUrl: './work-log-board.component.html',
  styleUrls: ['./work-log-board.component.scss'],
})
export class WorkLogBoardComponent implements OnInit {
  workLogs?: IWorkLog[];
  isLoading = false;
  public ticketStatusValues = Object.keys(TicketStatus);

  predicate = 'id';
  ascending = true;
  userId: any = null;
  username: any = null;
  public board: Board = new Board('Tickets Board', []);
  private tickets: ITicket[] | null = [];

  constructor(
    protected accountService: AccountService,
    protected workLogService: WorkLogService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected sortService: SortService,
    protected ticketService: TicketService,
    protected modalService: NgbModal
  ) {}

  trackId = (_index: number, item: IWorkLog): number => this.workLogService.getWorkLogIdentifier(item);

  ngOnInit(): void {
    this.accountService.identity().subscribe(value => {
      this.userId = value?.id;
      this.username = (value?.firstName || value?.login || 'John') + (value?.lastName || value?.login || 'Doe');
      this.load();
    });
  }

  public dropGrid(event: CdkDragDrop<string[]>): void {
    moveItemInArray(this.board.columns, event.previousIndex, event.currentIndex);
  }

  public drop(event: CdkDragDrop<number[]>): void {
    console.log('drop', event);
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);
    }
    this.extractAndSaveBoard();
  }

  delete(workLog: IWorkLog): void {
    const modalRef = this.modalService.open(WorkLogDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.workLog = workLog;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations())
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
  }

  load(): void {
    const queryObject: any = {
      page: 0,
      size: 100,
      'userId.equals': this.userId,
    };
    this.ticketService.query(queryObject).subscribe(value => {
      this.tickets = value.body;
      console.log(this.tickets);
      this.createBoard();
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.predicate, this.ascending);
  }

  getTicket(item: number) {
    return this.tickets?.find(f => f.id == item);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.predicate, this.ascending))
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.predicate = '';
    this.ascending = true;
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.workLogs = this.refineData(dataFromBody);
  }

  protected refineData(data: IWorkLog[]): IWorkLog[] {
    return data.sort(this.sortService.startSort(this.predicate, this.ascending ? 1 : -1));
  }

  protected fillComponentAttributesFromResponseBody(data: IWorkLog[] | null): IWorkLog[] {
    return data ?? [];
  }

  protected queryBackend(predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject = {
      sort: this.getSortQueryParam(predicate, ascending),
      'userId.equals': this.userId,
    };
    return this.workLogService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(predicate?: string, ascending?: boolean): void {
    const queryParamsObj = {
      sort: this.getSortQueryParam(predicate, ascending),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }

  private createBoard() {
    this.board.columns = [];
    for (const ticketStatusValue of this.ticketStatusValues) {
      this.board.columns.push(new Column(ticketStatusValue, ticketStatusValue, []));
    }
    if (this.tickets) {
      for (const ticket of this.tickets) {
        for (const column of this.board.columns) {
          if (ticket.status === column.id) {
            column.tasks.push(ticket.id);
          }
        }
      }
    }
  }

  handleRefresh() {
    console.log('handleRefresh');
    this.load();
  }

  addTicket(name: string) {
    const modalRef = this.modalService.open(TicketDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.userId = this.userId;
    modalRef.componentInstance.status = name;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      this.load();
    });
  }

  private extractAndSaveBoard() {
    for (const column of this.board.columns) {
      console.log(column.name);
      for (const task of column.tasks) {
        console.log(task);
        let find = this.tickets?.find(ticket => ticket.id == task);
        if (find && find.status != column.name) {
          find.status = column.name;
          this.ticketService.update(find).subscribe(value => {
            console.log('updated ticket', value.body);
          });
        }
      }
    }
  }
}
