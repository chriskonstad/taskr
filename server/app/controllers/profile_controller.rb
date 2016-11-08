class ProfileController < ApplicationController

  skip_before_action :verify_authenticity_token

  # Get user profile information
  def show
    id = [params[:id]]
    user = User.find_by!(id: id)
    render :json => user.as_json({:methods => [:avgRating,
                                               :paid,
                                               :earned,
                                               :rated,
                                               :rating,
                                               :ratings,
                                               :request,
                                               :actions]})
  end

  # Allow the user to both sign in and login in, depending on if the user exists
  def login
    name = params[:name]
    email = params[:email]
    fbid = params[:fbid]

    render nothing: true, status: :bad_request if name.nil? || email.nil? || fbid.nil?

    user = User.login(name, email, fbid)
    render json: { id: user.id }

    customer = Stripe::Customer.create(
      :email => params[:fbid]
    )
    
  end
end
