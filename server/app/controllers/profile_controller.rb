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

  # Create a new user profile
  def create
    user = User.create(name: params[:name],
                       email: params[:email],
                       wallet: params[:wallet])

    if user.save
      render json: user
    else
      render nothing: true, status: :bad_request
    end
  end
end

