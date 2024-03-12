import { Component } from '@angular/core'
import { ActivatedRoute, Router } from '@angular/router'
import { OrderDetailService } from './OrderDetail.service'
import { Util } from '../../util.service'

@Component({
  selector: 'orderDetail-edit',
  template: `
    <div class="container">
      <div class="row">
        <div class="col">
          <form ngNativeValidate method="post" (submit)="edit()">
            <div class="row">
              <div class="form-group col-md-6 col-lg-4">
                <label for="order_detail_order_id">Order Id</label>
                <input readonly id="order_detail_order_id" name="id.orderId" class="form-control form-control-sm" [(ngModel)]="orderDetail.id && orderDetail.id.orderId" type="number" required />
                <span *ngIf="errors.id" class="text-danger">{{errors.id}}</span>
              </div>
              <div class="form-group col-md-6 col-lg-4">
                <label for="order_detail_no">No</label>
                <input readonly id="order_detail_no" name="id.no" class="form-control form-control-sm" [(ngModel)]="orderDetail.id && orderDetail.id.no" type="number" required />
                <span *ngIf="errors.id" class="text-danger">{{errors.id}}</span>
              </div>
              <div class="form-group col-md-6 col-lg-4">
                <label for="order_detail_product_id">Product</label>
                <select id="order_detail_product_id" name="product.id" class="form-control form-control-sm" [(ngModel)]="orderDetail.product && orderDetail.product.id" required>
                  <option *ngFor="let product of products" value="{{product.id}}" [selected]="orderDetail.product && orderDetail.product.id == product.id">{{product.name}}</option>
                </select>
                <span *ngIf="errors.product" class="text-danger">{{errors.product}}</span>
              </div>
              <div class="form-group col-md-6 col-lg-4">
                <label for="order_detail_qty">Qty</label>
                <input id="order_detail_qty" name="qty" class="form-control form-control-sm" [(ngModel)]="orderDetail.qty" type="number" required />
                <span *ngIf="errors.qty" class="text-danger">{{errors.qty}}</span>
              </div>
              <div class="col-12">
                <a class="btn btn-sm btn-secondary" (click)="util.goBack('/orderDetail', $event)" routerLink="/orderDetail">Cancel</a>
                <button class="btn btn-sm btn-primary">Submit</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>`
})
export class OrderDetailEdit {
  
  orderDetail?: any = {product:{},id:{}}
  products?: any[]
  errors?: any = {}
  constructor(private router: Router, private route: ActivatedRoute, private OrderDetailService: OrderDetailService, public util: Util) { }
  
  ngOnInit() {
    this.get().add(() => {
      setTimeout(() => { this.util.initView(true) })
    })
  }

  get() {
    return this.OrderDetailService.edit(this.route.snapshot.params['orderId'], this.route.snapshot.params['no']).subscribe(data => {
      if (!data.orderDetail.product) {
        data.orderDetail.product = {}
      }
      if (!data.orderDetail.id) {
        data.orderDetail.id = {}
      }
      this.orderDetail = data.orderDetail
      this.products = data.products
    })
  }

  edit() {
    this.OrderDetailService.edit(this.route.snapshot.params['orderId'], this.route.snapshot.params['no'], this.orderDetail).subscribe(() => {
      this.util.goBack('/orderDetail')
    }, (e) => {
      if (e.error.errors) {
        this.errors = e.error.errors
      }
      else {
        alert(e.error.message)
      } 
    })
  }
}