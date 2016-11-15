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


    #retrieve the customer then charge the customer

    # Stripe.api_key = "sk_test_HWnsmI0iuGrMndEcCGbP7xtc"

    # customer = Stripe::Customer.create(
    #   :email => params[:id]
    # )
    

    # customer =  Stripe::Customer.retrieve(params[:id])
    

    #need to have the form

    # charge = Stripe::Charge.create(
    #   :customer    => customer.id,
    #   :amount      => trans.amount,
    #   :description => 'Rails Stripe customer',
    #   :currency    => 'usd'
    # )

    # rescue Stripe::CardError => e
    #   flash[:error] = e.message
   

  end

  def show
    t = Transaction.where(payer_id: params[:id]).first
    render :json => t.as_json
  end


  # curl https://api.stripe.com/v1/customers 
  # -u sk_test_ 
  # -d description ="Stripe Customer"

 

  private 

  def transaction_creation_params
  	params.require(:transaction).permit(:payer_id,
  									:payee_id,
  									:amount, 
  									:request_id)
  end


end
