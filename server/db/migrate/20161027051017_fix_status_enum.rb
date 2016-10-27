class FixStatusEnum < ActiveRecord::Migration
  def change
    # Default to OPEN
    change_column(:requests, :status, :integer, default: 0, null: false)
  end
end
