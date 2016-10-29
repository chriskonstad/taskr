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
                                               :request,
                                               :actions]})
  end

  # Allow the user to both sign in and login in, depending on if the user exists
  def login
    name = params[:name]
    email = params[:email]

    render nothing: true, status: :bad_request if name.nil? || email.nil?

    user = User.login(name, email)
    render json: { id: user.id }
  end
end
