class TransactionController < ApplicationController

  skip_before_action :verify_authenticity_token

  def new
  end

  def create
    # Amount in cents

    trans = Transaction.create(transaction_creation_params)

    if trans.id
    	render :json => {"id" => trans.id }.to_json
    else
    	render nothing: true, status: 500
    end

    customer = Stripe::Customer.create(
      :email => params[:stripeEmail],
      :source  => params[:stripeToken]
    )

    charge = Stripe::Charge.create(
      :customer    => trans.payer_id,
      :amount      => trans.amount,
      :description => 'Rails Stripe customer',
      :currency    => 'usd'
    )

    rescue Stripe::CardError => e
      flash[:error] = e.message
      redirect_to new_charge_path
   

  end

  def show
    t = Transaction.where(payer_id: params[:id]).first
    render :json => t.as_json
  end

 

  private 

  def transaction_creation_params
  	params.require(:transaction).permit(:payer_id,
  									:payee_id,
  									:amount, 
  									:request_id)
  end



end
