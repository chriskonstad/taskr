class RequestController < ApplicationController

  skip_before_action :verify_authenticity_token

  # Show request as JSON given an ID
  def show
    r = Request.where(id: params[:id]).first
    render :json => r.as_json
  end

  # Show all nearby open requests
  def nearby
    long = params[:long].to_f
    lat = params[:lat].to_f
    radius_miles = params[:radius].to_f

    if(!params.has_key?(:long) ||
        !params.has_key?(:lat) ||
        !params.has_key?(:radius))
      render nothing: true, status: 404
    else
      requests = Request.openNear(long, lat, radius_miles)
      render :json => requests.as_json
    end
  end

  # Create a request with JSON parameters
  def create
    json = params[:request]
    req = Request.create(creation_params)
    if req.id
      render :json => { "id" => req.id }.to_json
    else
      render nothing: true, status: 500
    end
  end

  # Let the user edit a request, EXCLUDING some values (id, status)
  def edit
    id = params[:id]
    user_id = params[:auth][:user_id]

    json = params[:request]
    req = Request.find_by(id: id, user_id: user_id)
    if req
      req.update(edit_params)
      render nothing: true
    else
      render nothing: true, status: 400
    end
  end

  # Let user accept a request
  def accept
    id = params[:params][:id]
    user_id = params[:auth][:user_id]

    req = Request.find_by(id: id)
    if req
      # Make sure user can only accept open requests
      if !req.open?
        render nothing: true, status: 403
      else
        req.update(status: Request.statuses[:accepted],
                   actor_id: user_id)
        render nothing: true
      end
    else
      render nothing: true, status: 400
    end
  end

  # Let user reject a request
  def reject
    id = params[:params][:id]
    user_id = params[:auth][:user_id]

    req = Request.find_by(id: id, actor_id: user_id)
    if req
      # Make sure user can only reject accepted requests
      if !req.accepted?
        render nothing: true, status: 403
      else
        req.update(status: Request.statuses[:open],
                   actor_id: nil)
        render nothing: true
      end
    else
      render nothing: true, status: 400
    end
  end

  # Let user complete a request
  def complete
    id = params[:params][:id]
    user_id = params[:auth][:user_id]

    req = Request.find_by(id: id, actor_id: user_id)
    if req
      # Make sure user can only complete accepted requests
      if !req.accepted?
        render nothing: true, status: 403
      else
        req.update(status: Request.statuses[:completed])
        # TODO Notify the original poster???
        render nothing: true
      end
    else
      render nothing: true, status: 400
    end
  end

  # Let user pay a request
  def pay
    id = params[:params][:id]
    user_id = params[:auth][:user_id]

    req = Request.find_by(id: id, user_id: user_id)
    if req
      # Make sure user can only complete accepted requests
      if !req.completed?
        render nothing: true, status: 403
      else
        req.update(status: Request.statuses[:paid])
        # TODO Create a Transaction to pay the actor
        render nothing: true
      end
    else
      render nothing: true, status: 400
    end
  end

  # Let user cancel a request
  def cancel
    id = params[:params][:id]
    user_id = params[:auth][:user_id]

    req = Request.find_by(id: id, user_id: user_id)
    if req
      # Make sure user can only cancel open requests
      if !req.open?
        render nothing: true, status: 403
      else
        req.update(status: Request.statuses[:canceled])
        render nothing: true
      end
    else
      render nothing: true, status: 400
    end
  end

  private
    def creation_params
      params.require(:request).permit(:title,
                                      :user_id,
                                      :amount,
                                      :lat,
                                      :long,
                                      :due,
                                      :description)
    end

    def edit_params
      params.require(:request).permit(:title,
                                      :amount,
                                      :lat,
                                      :long,
                                      :due,
                                      :description)
    end
end

