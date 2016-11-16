class TransactionController < ApplicationController

  skip_before_action :verify_authenticity_token

  def new
  end

  def create
    # Amount in cents
    
    trans = Transaction.create(transaction_creation_params)

    if trans.id
    	# render :json => {"id" => trans.id }.to_json
      # customer = Stripe::Customer.retrieve('cus_9ZUHAPLnqbdgyF')
      
      # Stripe::Customer.create
      #   :email => "1234",
      #   :description => "testing user",
      #   :source => "tok_19GLJLAHXVnt8dzeeAzTI33f"
      # }

      token = Stripe::Token.create(
          :card => {
            :number => "4242424242424242",
            :exp_month => 11,
            :exp_year => 2017,
            :cvc => "314"
          },
        );
      


      # ct = Stripe::Customer.create(
      #   :email => '1234',
      #   :description => 'testing user',
      #   :source => token
      # )


      #to search by the email
      customers = Stripe::Customer.list(:limit => 1)

      customer = customers.select do |c|
        c.email == [params[:id]]
      end

      #amount = trans.amount

      Stripe::Charge.create(
        :amount => trans.amount.round, #is equal to 2 dollars
        :currency => "usd",
        :source => token, # obtained with Stripe.js
        :description => "Charge for 1234@example.com"
      )

      # render :json => {"customer" => customer}.to_json
      render :json => {"token" => token}.to_json
    else
    	render nothing: true, status: 500
    end

    #token tok_19GLziAHXVnt8dzezIu3kmEj

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
