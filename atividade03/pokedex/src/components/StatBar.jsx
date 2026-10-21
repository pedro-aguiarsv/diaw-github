export default function StatBar({ label, value, color }) {
  const width = Math.min(100, Math.round((value / 255) * 100))

  return (
    <div className="stat">
      <div className="stat-label">
        <span>{label}</span>
        <strong>{value}</strong>
      </div>
      <div className="stat-track">
        <span style={{ width: `${width}%`, background: color }} />
      </div>
    </div>
  )
}
