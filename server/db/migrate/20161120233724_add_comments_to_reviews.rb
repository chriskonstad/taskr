class AddCommentsToReviews < ActiveRecord::Migration
  def change
    add_column :reviews, :comment, :string
  end
end
